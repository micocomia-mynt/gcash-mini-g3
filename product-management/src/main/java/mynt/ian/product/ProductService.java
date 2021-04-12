package mynt.ian.product;

import mynt.ian.product.exception.ProductNotFoundException;
import mynt.ian.product.payload.CreateActivityRequest;
import mynt.ian.product.payload.Product;
import mynt.ian.product.payload.ProductEntry;
import mynt.ian.product.repository.ProductRepository;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;

    @Value("${gcash.mini.activityUrl}")
    private String reqUrl;

    public ProductService(ProductRepository productRepository, RestTemplate restTemplate) {
        this.productRepository = productRepository;
        this.restTemplate = restTemplate;
    }

    public String addProduct(String name, float price) {

        // Generate product ID
        String productId = RandomStringUtils.randomAlphanumeric(8);

        ProductEntry entry = new ProductEntry();
        entry.setProductId(productId);
        entry.setName(name);
        entry.setPrice(price);

        productRepository.save(entry);
        recordActivity("ADD_PRODUCT", "productId="+productId, "");
        return productId;

    }

    public Product getProduct(String productId) throws ProductNotFoundException {

        Optional<ProductEntry> entry = productRepository.findById(productId);
        if (entry.isEmpty()) {
            throw new ProductNotFoundException("Product with id {} not found", productId);
        }

        Product product = new Product();
        product.setName(entry.get().getName());
        product.setPrice(entry.get().getPrice());

        recordActivity("GET_PRODUCT", "productId="+productId, "");
        return product;
    }

    public void recordActivity(String action, String identifier, String details) {
        CreateActivityRequest request = new CreateActivityRequest();
        request.setAction(action);
        request.setIdentifier(identifier);
        request.setDetails(details);

        ResponseEntity<Object> response = restTemplate.postForEntity(reqUrl, request, Object.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            LOGGER.info("Activity recorded!");
        } else {
            LOGGER.warn("Activity not recorded!");
        }
    }

}
