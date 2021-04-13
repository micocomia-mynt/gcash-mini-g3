package ph.apper.account.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ph.apper.account.App;
import ph.apper.account.controller.AccountController;
import ph.apper.account.domain.Account;
import ph.apper.account.domain.PendingAccount;
import ph.apper.account.exception.*;
import ph.apper.account.payload.AccountData;
import ph.apper.account.payload.CreateActivityRequest;
import ph.apper.account.payload.UpdateAccountRequest;
import ph.apper.account.util.IdService;

import java.util.*;

@Service
public class AccountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final Map<String, PendingAccount> pendingAccounts = new HashMap<>();
    private final Map<String, Account> accounts = new HashMap<>();

    private final AmazonSQS amazonSQS;
    private final App.SqsProperties sqsProperties;

    public AccountService(AmazonSQS amazonSQS, App.SqsProperties sqsProperties) {
        this.amazonSQS = amazonSQS;
        this.sqsProperties = sqsProperties;
    }

    public String createAccount(String email, String firstName, String lastName, String clearPassword) throws AccountRegistrationException, JsonProcessingException {
        if (accounts.containsKey(email)) {
            recordActivity("EMAIL_EXISTING", "email="+email, "");
            throw new AccountRegistrationException("email already registered");
        }

        String verificationCode = RandomStringUtils.randomAlphanumeric(10);
        PendingAccount pendingAccount = new PendingAccount();

        pendingAccount.setFirstName(firstName);
        pendingAccount.setLastName(lastName);
        pendingAccount.setPassword(BCrypt.withDefaults().hashToString(4, clearPassword.toCharArray()));
        pendingAccount.setVerificationCode(verificationCode);
        pendingAccount.setBalance(0);

        pendingAccounts.put(email, pendingAccount);

        recordActivity("PENDING_REG", "email="+email, pendingAccount.toString());

        return verificationCode;
    }

    public void verify(String verificationCode, String email) throws AccountVerificationException, JsonProcessingException {
        String accountId = IdService.generateId();

        Optional<Account> optAccount = pendingAccounts.entrySet().stream()
                .filter(entry -> email.equals(entry.getKey()))
                .filter(entry -> verificationCode.equals(entry.getValue().getVerificationCode()))
                .map(entry -> {
                    Account account = new Account();
                    account.setFirstName(entry.getValue().getFirstName());
                    account.setLastName(entry.getValue().getLastName());
                    account.setPassword(entry.getValue().getPassword());
                    account.setBalance(entry.getValue().getBalance());
                    account.setAccountId(accountId);
                    account.setLoggedIn(false);

                    return account;
                })
                .findFirst();

        if (optAccount.isEmpty()) {
            recordActivity("VER_FAILED", "email="+email, "Invalid details.");
            throw new AccountVerificationException("Invalid verification details");
        }

        recordActivity("ACCT_VERIFIED", "email="+email, optAccount.get().toString());

        accounts.put(email, optAccount.get());
    }

    public boolean login(String email, String clearPassword) throws AccountAuthenticationException, JsonProcessingException {
        StringBuilder accountStatus = new StringBuilder("");

        if (accounts.containsKey(email)) {
            Account account = accounts.get(email);
            BCrypt.Result passwordVerification = BCrypt.verifyer().verify(clearPassword.toCharArray(), account.getPassword());

            if (passwordVerification.verified){
                if(account.isLoggedIn()){
                    accountStatus.append("Logged off");

                    account.setLoggedIn(false);
                    LOGGER.info("Account logged out.");
                }else{
                    accountStatus.append("Logged in");

                    account.setLoggedIn(true);
                    LOGGER.info("Account logged in.");
                }
            }else{
                LOGGER.warn("Invalid login.");
            }

            recordActivity("ACCT_AUTH", "email="+email, accountStatus.toString());
            return passwordVerification.verified;
        }

        recordActivity("ACCT_NOT_AUTH", "email="+email, accountStatus.toString());
        throw new AccountAuthenticationException("Invalid login");
    }

    public AccountData getAccountDetails(String id) throws AccountNotFoundException, JsonProcessingException {
        AccountData accountData = new AccountData();

        Optional<Account> optAccount = accounts.entrySet().stream()
                .filter(entry -> id.equals(entry.getValue().getAccountId()))
                .map(entry -> {
                    Account account = accounts.get(entry.getKey());
                    accountData.setFirstName(account.getFirstName());
                    accountData.setLastName(account.getLastName());
                    accountData.setEmail(entry.getKey());
                    accountData.setBalance(account.getBalance());
                    accountData.setLoggedIn(account.isLoggedIn());

                    return account;
                }).findFirst();

        if(optAccount.isEmpty()) {
            recordActivity("ACCT_NOT_FOUND", "id="+id, "");
            throw new AccountNotFoundException("Account " + id + " not found");
        }

        LOGGER.info("Got Account " + id);
        recordActivity("ACCT_RETRIEVAL", "id="+id, "");
        return accountData;
    }

    public String getAccountId(String email) throws AccountNotFoundException{
        Optional<String> optAccountId = accounts.entrySet().stream()
                .filter(entry -> email.equals(entry.getKey()))
                .map(entry -> {
                    return entry.getValue().getAccountId();
                })
                .findFirst();

        if (optAccountId.isEmpty()){
            throw new AccountNotFoundException("Account does not exist.");
        }

        return optAccountId.get();
    }

    public Account getAccount(String id) throws AccountNotFoundException {
        Optional<Account> optAccount = accounts.entrySet().stream()
                .filter(entry -> id.equals(entry.getValue().getAccountId()))
                .map(entry -> {
                    Account account = accounts.get(entry.getKey());
                    return account;
                }).findFirst();

        if(optAccount.isEmpty()) {
            throw new AccountNotFoundException("Account " + id + " not found");
        }

        return optAccount.get();
    }

    public List<AccountData> getAccounts() throws JsonProcessingException {
        List<AccountData> accountList = new ArrayList<>();

        for (Map.Entry<String,Account> entry : accounts.entrySet()) {
            AccountData accountData = new AccountData();
            accountData.setBalance(entry.getValue().getBalance());
            accountData.setEmail(entry.getKey());
            accountData.setFirstName(entry.getValue().getFirstName());
            accountData.setLastName(entry.getValue().getLastName());
            accountData.setLoggedIn(entry.getValue().isLoggedIn());

            accountList.add(accountData);
        }

        LOGGER.info("Got Accounts");
        recordActivity("ALL_ACCT_RETRIEVAL", "GET", "Get all activities");
        return accountList;
    }

    public void updateAccount(String id, UpdateAccountRequest request) throws AccountNotLoggedInException, JsonProcessingException {
        Account account = new Account();
        AccountData accountData = new AccountData();

        try{
            account = getAccount(id);
            accountData =  getAccountDetails(id);
        }catch(AccountNotFoundException e){
            LOGGER.error("Account not found", e);
        }

        // Check if user is logged in
        if(account.isLoggedIn()) {
            String email = accountData.getEmail();

            if (request.getBalance() != account.getBalance()) {
                account.setBalance(request.getBalance());
                LOGGER.info("Balance updated.");
                recordActivity("ACCT_UPDATE", "email="+email, "Email updated");
            }

            try {
                if (!request.getPassword().isEmpty()) {
                    String newPassword = BCrypt.withDefaults().hashToString(4, request.getPassword().toCharArray());
                    account.setPassword(newPassword);
                    account.setLoggedIn(false);

                    LOGGER.info("Logged out. Use new password to login.");
                    recordActivity("ACCT_UPDATE", "email="+email, "Password updated");
                }
            } catch (NullPointerException e) {
                LOGGER.info("Password not changed.");
            }

            accounts.put(email, account);
        }else{
            throw new AccountNotLoggedInException("Account " + id + " is not logged in.");
        }
    }

    private void recordActivity(String action, String identifier, String details) throws JsonProcessingException {
        CreateActivityRequest request = new CreateActivityRequest();
        request.setAction(action);
        request.setIdentifier(identifier);
        request.setDetails(details);

        String message = OBJECT_MAPPER.writeValueAsString(request);
        SendMessageRequest sendMessageRequest = new SendMessageRequest(sqsProperties.getQueueUrl(), message);
        amazonSQS.sendMessage(sendMessageRequest);
    }
}
