package ph.apper.finance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ph.apper.finance.payload.AccountData;
import ph.apper.finance.util.Activity;
import ph.apper.finance.domain.AddMoneyTransaction;
import ph.apper.finance.repository.AddMoneyTransactionsRepository;
import ph.apper.finance.repository.TransferTransactionsRepository;
import ph.apper.finance.domain.TransferTransaction;
import ph.apper.finance.util.IdService;

import java.lang.Double;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("finances")
public class TransactionController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);
    private final TransferTransactionsRepository transferTransactionsRepository;
    private final AddMoneyTransactionsRepository addMoneyTransactionsRepository;

    @Autowired
    private final RestTemplate restTemplate;


    public TransactionController(TransferTransactionsRepository transferTransactionsRepository,
                                 AddMoneyTransactionsRepository addMoneyTransactionsRepository,
                                 RestTemplate restTemplate) {
        this.transferTransactionsRepository = transferTransactionsRepository;
        this.addMoneyTransactionsRepository = addMoneyTransactionsRepository;
        this.restTemplate = restTemplate;
    }


    @PostMapping(path = "/transfer")
    public ResponseEntity transfer(@RequestBody TransferRequest request) {

        LOGGER.info("Transfer Request: " + request);
        try {
            String fromAccountId = request.getFromAccountId();
            String toAccountId = request.getToAccountId();
            activityRequest(request, TransactionType.TRANSFER);

            Map senderAccount = getAccountRequest(fromAccountId);
            Map receiverAccount = getAccountRequest(toAccountId);

            LOGGER.info("DEBUG THIS: "+ receiverAccount);

            if(senderAccount.get("logged_in") == Boolean.TRUE) {
                return transferProcess(senderAccount, receiverAccount, request);
            } else {
                LOGGER.info("User not logged in.");
                return ResponseEntity.badRequest().build();
            }

        } catch( Exception err){
            createTransaction(request, Boolean.FALSE, TransactionType.TRANSFER);
            LOGGER.error("Transfer method error: " + err);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(path = "/add")
    public ResponseEntity addMoney(@RequestBody AddMoneyRequest request) {

        LOGGER.info("Add money request: " + request);
        try {
            String AccountId = request.getAccountId();

            activityRequest(request, TransactionType.ADD);
            Map userAccount = getAccountRequest(AccountId);

            LOGGER.info("USER ACCOUNT:" + userAccount);
            if(userAccount.get("logged_in") == Boolean.TRUE) {
                LOGGER.info("USER ACCOUNT:" + userAccount);
                return addMoneyProcess(userAccount, request);
            }
            return ResponseEntity.ok().build();
        } catch( Exception err){
            createTransaction(request, Boolean.FALSE, TransactionType.ADD);
            LOGGER.error("addMoney method error: " + err);
            return ResponseEntity.badRequest().build();
        }
    }



    private void activityRequest(Object request, TransactionType transactionType) {
        try {
            switch(transactionType) {
                case TRANSFER:{
                    if(request instanceof TransferRequest) {
                        TransferRequest transferRequest = (TransferRequest) request;
                        Activity transferActivity = new Activity();
                        transferActivity.setAction("Transfer Money");
                        transferActivity.setIdentifier("accountId=" + transferRequest.getFromAccountId());
                        transferActivity.setDetails("Recipient user id: " + transferRequest.getToAccountId());

                        ResponseEntity<Object> response = restTemplate.postForEntity("http://localhost:8084/activity",
                                                                                        transferActivity,
                                                                                        Object.class);

                        if (response.getStatusCode().is2xxSuccessful()) {
                            LOGGER.info("Transfer Activity Record: Success.");
                        } else {
                            LOGGER.info("Err: " + response.getStatusCode());
                        }
                    }
                }
                case ADD: {
                    if (request instanceof AddMoneyRequest) {
                        AddMoneyRequest addMoneyRequest = (AddMoneyRequest) request;
                        Activity addMoneyActivity = new Activity();
                        addMoneyActivity.setAction("Add Money");
                        addMoneyActivity.setIdentifier("accountId=" + addMoneyRequest.getAccountId());
                        addMoneyActivity.setDetails("Amount: " + addMoneyRequest.getAmount());

                        ResponseEntity<Object> response = restTemplate.postForEntity("http://localhost:8084/activity",
                                                                                        addMoneyActivity,
                                                                                        Object.class);
                        if (response.getStatusCode().is2xxSuccessful()) {
                            LOGGER.info("Add Activity Record: Success");
                        } else {
                            LOGGER.info("Err: " + response.getStatusCode());
                        }
                    }
                }

            }
        } catch (Exception err) {
            LOGGER.error("activityRequest: " + err);
        }
    }



    private Map getAccountRequest(String accountId){
        ObjectMapper mapper = new ObjectMapper();
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity("http://localhost:8081/account/" + accountId, Map.class);
            LOGGER.info(String.valueOf(response.getBody()));

            if(response.getBody() != null) {
//                @SuppressWarnings("unchecked")
//                Map<String, Object> responseBody = mapper.readValue((String) response.getBody(), HashMap.class)
                return response.getBody();
            }

        } catch (Exception err){
            LOGGER.error("getAccountRequest error: " + err);
        }
        return null;
    }



    private ResponseEntity transferProcess(Map senderAccount, Map receiverAccount, TransferRequest request){


        if(((Double) senderAccount.get("balance")).compareTo(request.getAmount()) >= 0 && receiverAccount != null){

            try{
                Double newSenderBalance = ((Double)senderAccount.get("balance"))-(request.getAmount());
                Double newReceiverBalance = ((Double)receiverAccount.get("balance"))+(request.getAmount());

                //Create patch request to account management API:
                UpdateAccountRequest updateAccountRequest = new UpdateAccountRequest();
                updateAccountRequest.setBalance(newSenderBalance);
                updateAccountRequest.setPassword(request.getSenderPassword());

                String url_sender = "http://localhost:8081/account/" + request.getFromAccountId();
                AccountData senderResponse = restTemplate.patchForObject(url_sender, updateAccountRequest, AccountData.class);


                LOGGER.info("Deducted {} amount from {} for transfer", request.getAmount(), request.getFromAccountId());


                updateAccountRequest.setBalance(newReceiverBalance);
                updateAccountRequest.setPassword(request.getReceiverPassword());

                LOGGER.info("Got this far:" + updateAccountRequest.getBalance() + " " + updateAccountRequest.getPassword());

                String url_receiver = "http://localhost:8081/account/" + request.getToAccountId();
                LOGGER.info(url_receiver);

                AccountData receiverResponse = restTemplate.patchForObject(url_receiver, updateAccountRequest, AccountData.class);
                LOGGER.info("{} received {} from {}", request.getToAccountId(), request.getAmount(), request.getFromAccountId());
                createTransaction(request, Boolean.TRUE, TransactionType.TRANSFER);

                return ResponseEntity.ok().build();

            } catch( Exception err){
                createTransaction(request, Boolean.FALSE, TransactionType.TRANSFER);
                LOGGER.error("transferProcess Error: " + err);
                err.printStackTrace(System.out);
                return  ResponseEntity.badRequest().build();
            }

        } else {
            createTransaction(request, Boolean.FALSE, TransactionType.TRANSFER);
            return  ResponseEntity.badRequest().build();
        }
    }


    private ResponseEntity addMoneyProcess(Map userAccount, AddMoneyRequest request){
            LOGGER.info(String.valueOf(userAccount));
            try{
                Double newUserBalance = ((Double)userAccount.get("balance"))+(request.getAmount());
                UpdateAccountRequest updateAccountRequest = new UpdateAccountRequest();
                updateAccountRequest.setBalance(newUserBalance);
                updateAccountRequest.setPassword(request.getPassword());

                //Create patch request to account management API:
                String url = "http://localhost:8081/account/" + request.getAccountId();
                AccountData response = restTemplate.patchForObject(url, updateAccountRequest, AccountData.class);
                LOGGER.info("Added {} balance to {}", request.getAmount(), request.getAccountId());
                createTransaction(request, Boolean.TRUE, TransactionType.TRANSFER);
                return ResponseEntity.ok().build();

            } catch( Exception err){
                LOGGER.error("Catcher 4: " + err);
                createTransaction(request, Boolean.FALSE, TransactionType.TRANSFER);
                return ResponseEntity.badRequest().build();
            }

    }



    private void createTransaction(Object request, Boolean transactionSuccess, TransactionType transactionType){
        //Switch case for transfer transaction and add money transaction
        switch (transactionType) {
            case TRANSFER: {
                if(request instanceof TransferRequest) {
                    TransferRequest transferRequest = (TransferRequest) request;

                    TransferTransaction transferTransaction = new TransferTransaction();
                    transferTransaction.setTransactionId(IdService.getNextTransactionId());
                    transferTransaction.setFromAccountId(transferRequest.getFromAccountId());
                    transferTransaction.setToAccountId(transferRequest.getToAccountId());
                    transferTransaction.setTransactionSuccess(transactionSuccess);
                    transferTransaction.setAmount(transferRequest.getAmount());
                    transferTransaction.setTimestamp(LocalDateTime.now());
                    transferTransactionsRepository.save(transferTransaction);
                }
            }
            case ADD: {
                if(request instanceof AddMoneyRequest) {
                    AddMoneyRequest addMoneyRequest = (AddMoneyRequest) request;
                    AddMoneyTransaction addMoneyTransaction = new AddMoneyTransaction();
                    addMoneyTransaction.setTransactionId(IdService.getNextTransactionId());
                    addMoneyTransaction.setAccountId(addMoneyRequest.getAccountId());
                    addMoneyTransaction.setTransactionSuccess(transactionSuccess);
                    addMoneyTransaction.setAmount(addMoneyRequest.getAmount());
                    addMoneyTransaction.setTimestamp(LocalDateTime.now());
                    addMoneyTransactionsRepository.save(addMoneyTransaction);
                }
            }
        }
    }

}