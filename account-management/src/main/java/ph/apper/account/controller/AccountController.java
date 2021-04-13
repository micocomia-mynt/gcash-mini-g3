package ph.apper.account.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ph.apper.account.exception.*;
import ph.apper.account.payload.*;
import ph.apper.account.service.AccountService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("account")
public class AccountController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<CreateAccountResponse> create(@Valid @RequestBody CreateAccountRequest request) {
        try {
            String verificationCode = accountService.createAccount(request.getEmail(), request.getFirstName(), request.getLastName(), request.getPassword());

            CreateAccountResponse response = new CreateAccountResponse();
            response.setVerificationCode(verificationCode);

            return ResponseEntity.ok(response);
        } catch (AccountRegistrationException | JsonProcessingException e) {
            LOGGER.error("Failed to register account", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<GenericResponse> verify(@Valid @RequestBody VerifyAccountRequest request) {
        try {
            accountService.verify(request.getVerificationCode(), request.getEmail());
            LOGGER.info("Verified account");

            return ResponseEntity.ok(new GenericResponse("Verified account"));
        } catch (AccountVerificationException | JsonProcessingException e) {
            LOGGER.error("Failed to verify account", e);

            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticateAccountResponse> login(@Valid @RequestBody AuthenticateAccountRequest request) {
        try {
            boolean isValid = accountService.login(request.getEmail(), request.getPassword());

            if (isValid) {
                AuthenticateAccountResponse response = new AuthenticateAccountResponse();
                response.setAccountId(accountService.getAccountId(request.getEmail()));

                return ResponseEntity.ok(response);
            }
        } catch (AccountAuthenticationException | AccountNotFoundException | JsonProcessingException e) {
            LOGGER.error("Failed to authenticate account", e);
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{Id}")
    public ResponseEntity<AccountData> getAccount(@PathVariable String Id){
        try{
            AccountData accountData = accountService.getAccountDetails(Id);
            return ResponseEntity.ok(accountData);

        }catch (AccountNotFoundException | JsonProcessingException e){
            LOGGER.error("Account not found", e);
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping
    public ResponseEntity<List<AccountData>>getAccounts(){
        try{
            List<AccountData> accountList = accountService.getAccounts();
            return ResponseEntity.ok(accountList);
        }catch (JsonProcessingException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{Id}")
    public ResponseEntity<AccountData> updateAccount(@PathVariable String Id, @Valid @RequestBody UpdateAccountRequest request){
        try{
            accountService.updateAccount(Id, request);
        }catch (AccountNotLoggedInException | JsonProcessingException e){
            LOGGER.error("Account not logged in.", e);
            return ResponseEntity.badRequest().build();
        }

        try{
            AccountData accountData = accountService.getAccountDetails(Id);

            LOGGER.info("Updated account " + Id);
            return ResponseEntity.ok(accountData);
        }catch (AccountNotFoundException | JsonProcessingException e){
            LOGGER.error("Account not found", e);
        }

        return ResponseEntity.badRequest().build();
    }
}
