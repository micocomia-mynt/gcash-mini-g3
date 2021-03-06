package ph.apper.gateway;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ph.apper.gateway.payload.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("account")
public class AccountController {
    private final RestTemplate restTemplate;
    private final App.GCashMiniProperties gCashMiniProperties;

    public AccountController(RestTemplate restTemplate, App.GCashMiniProperties gCashMiniProperties) {
        this.restTemplate = restTemplate;
        this.gCashMiniProperties = gCashMiniProperties;
    }

    @PostMapping
    public ResponseEntity<CreateAccountResponse> create(@Valid @RequestBody CreateAccountRequest request) {
        ResponseEntity<CreateAccountResponse> response = restTemplate.postForEntity(gCashMiniProperties.getAccountUrl(), request, CreateAccountResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            CreateAccountResponse verificationCode = response.getBody();
            return ResponseEntity.ok(verificationCode);
        }

        return ResponseEntity.status(response.getStatusCode()).build();
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@Valid @RequestBody VerifyAccountRequest request){
        String url = gCashMiniProperties.getAccountUrl() + "/verify";
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(response.getBody());
        }

        return ResponseEntity.status(response.getStatusCode()).build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticateAccountResponse> login(@Valid @RequestBody AuthenticateAccountRequest request) {
        String url = gCashMiniProperties.getAccountUrl() + "/authenticate";
        ResponseEntity<AuthenticateAccountResponse> response = restTemplate.postForEntity(url, request, AuthenticateAccountResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            AuthenticateAccountResponse accountId = response.getBody();
            return ResponseEntity.ok(accountId);
        }

        return ResponseEntity.status(response.getStatusCode()).build();
    }

    @GetMapping("/{Id}")
    public ResponseEntity<AccountData> getAccount(@PathVariable String Id){
        String url = gCashMiniProperties.getAccountUrl() + "/" + Id;
        ResponseEntity<AccountData> response = restTemplate.getForEntity(url, AccountData.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            AccountData accountData = response.getBody();
            return ResponseEntity.ok(accountData);
        }

        return ResponseEntity.status(response.getStatusCode()).build();
    }

    @PatchMapping("/{Id}")
    public ResponseEntity<AccountData> updateAccount(@PathVariable String Id, @Valid @RequestBody UpdateAccountRequest request){
        String url = gCashMiniProperties.getAccountUrl() + "/" + Id;

        try{
            AccountData response = restTemplate.patchForObject(url, request, AccountData.class);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }
}
