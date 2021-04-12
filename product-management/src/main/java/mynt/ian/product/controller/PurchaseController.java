package mynt.ian.product.controller;

import mynt.ian.product.ProductService;
import mynt.ian.product.payload.AccountData;
import mynt.ian.product.payload.ProductEntry;
import mynt.ian.product.payload.PurchaseProduct;
import mynt.ian.product.payload.UpdateAccountRequest;
import mynt.ian.product.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("purchase")
public class PurchaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseController.class);
    private final ProductRepository productRepository;
    private final ProductService productService;

    @Value("${gcash.mini.accountUrl}")
    private String reqUrl;

    private final RestTemplate restTemplate;

    public PurchaseController(ProductRepository productRepository, ProductService productService, RestTemplate restTemplate) {
        this.productRepository = productRepository;
        this.productService = productService;
        this.restTemplate = restTemplate;
    }

    @PostMapping
    public ResponseEntity purchase(@Valid @RequestBody PurchaseProduct request) {

        String productId = request.getProductId();
        String accountId = request.getAccountId();

        // Check if productId exists. If yes, retrieve product price
        Optional<ProductEntry> entry = productRepository.findById(productId);
        if (entry.isEmpty()) {
            LOGGER.error("ERROR: Product with id {} not found", productId);
            return ResponseEntity.notFound().build();
        }

        float price = entry.get().getPrice();

        // Check balance of accountId
        String url = reqUrl + "/" + accountId;
        ResponseEntity<AccountData> response = restTemplate.getForEntity(url, AccountData.class);

        // Check if API call to Account-Management is successful
        if (response.getStatusCode().is2xxSuccessful()) {
            LOGGER.info("SUCCESSFUL: Account with id {} found", accountId);
        } else {
            LOGGER.error("ERROR: Account with id {} not found", accountId);
            return ResponseEntity.notFound().build();
        }

        // Get account balance
        float balance = response.getBody().getBalance();

        // Check if balance is enough to buy the product
        if (balance < price) {
            LOGGER.error("ERROR: Balance {} less than product price {}", balance, price);
            return ResponseEntity.badRequest().build();
        }

        float newBalance = balance - price;

        // Update account balance
        UpdateAccountRequest updateRequest = new UpdateAccountRequest();
        updateRequest.setBalance(newBalance);
        AccountData updateResponse = restTemplate.patchForObject(url, updateRequest, AccountData.class);

        if (updateResponse == null) {
            LOGGER.error("ERROR: Balance not updated");
            return ResponseEntity.badRequest().build();
        }

        productService.recordActivity("PURCHASE_PRODUCT", "accountId="+accountId, "");
        return ResponseEntity.ok().build();
    }

}
