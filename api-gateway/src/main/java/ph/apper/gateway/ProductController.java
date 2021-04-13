package ph.apper.gateway;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ph.apper.gateway.payload.Activity;
import ph.apper.gateway.payload.Product;
import ph.apper.gateway.payload.ProductResponse;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("product")
public class ProductController {
    private final RestTemplate restTemplate;
    private final App.GCashMiniProperties gCashMiniProperties;

    public ProductController(RestTemplate restTemplate, App.GCashMiniProperties gCashMiniProperties) {
        this.restTemplate = restTemplate;
        this.gCashMiniProperties = gCashMiniProperties;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> addProduct(@Valid @RequestBody Product request) {
        ResponseEntity<ProductResponse> response = restTemplate.postForEntity(
                gCashMiniProperties.getProductsUrl(),
                request,
                ProductResponse.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(response.getBody());
        }

        return ResponseEntity.status(response.getStatusCode()).build();
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        ResponseEntity<Product[]> response = restTemplate.getForEntity(
                gCashMiniProperties.getProductsUrl(),
                Product[].class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            List<Product> products = Arrays.asList(response.getBody());
            return ResponseEntity.ok(products);
        }

        return ResponseEntity.status(response.getStatusCode()).build();

    }

    @GetMapping("{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable("productId") String productId) {
        ResponseEntity<Product> response = restTemplate.getForEntity(
                gCashMiniProperties.getProductsUrl() + "/" + productId,
                Product.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(response.getBody());
        }

        return ResponseEntity.status(response.getStatusCode()).build();

    }

}
