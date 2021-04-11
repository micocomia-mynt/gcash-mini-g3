package mynt.ian.product.controller;

import mynt.ian.product.payload.AccountData;
import mynt.ian.product.payload.ProductEntry;
import mynt.ian.product.payload.PurchaseProduct;
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

@RestController
@RequestMapping("purchase")
public class PurchaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseController.class);
    private final ProductRepository productRepository;

    @Value("${gcash.mini.accountUrl}")
    private String reqUrl;

    private final RestTemplate restTemplate;


    public PurchaseController(ProductRepository productRepository, RestTemplate restTemplate) {
        this.productRepository = productRepository;
        this.restTemplate = restTemplate;
    }

    @PostMapping
    public ResponseEntity purchase(@RequestBody PurchaseProduct request) {

        String productId = request.getProductId();
        String accountId = request.getAccountId();

        // Check if productId exists. If yes, retrieve product price
        if (!productRepository.existsById(productId)) {
            LOGGER.error("ERROR: Product with id {} not found", productId);
            return ResponseEntity.notFound().build();
        }
        ProductEntry entry = productRepository.findById(productId).get();
        float price = entry.getPrice();

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

        return ResponseEntity.ok().build();
    }

}
