package mynt.ian.product;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mynt.ian.product.exception.ProductNotFoundException;
import mynt.ian.product.payload.Activity;
import mynt.ian.product.payload.CreateActivityRequest;
import mynt.ian.product.payload.Product;
import mynt.ian.product.payload.ProductEntry;
import mynt.ian.product.repository.ProductRepository;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class ProductService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;

    private final AmazonSQS amazonSQS;
    private final ProductApp.SqsProperties sqsProperties;

    @Value("${gcash.mini.activityUrl}")
    private String reqUrl;

    public ProductService(ProductRepository productRepository, RestTemplate restTemplate, AmazonSQS amazonSQS, ProductApp.SqsProperties sqsProperties) {
        this.productRepository = productRepository;
        this.restTemplate = restTemplate;
        this.amazonSQS = amazonSQS;
        this.sqsProperties = sqsProperties;
    }

    public String addProduct(String name, float price) throws JsonProcessingException {

        // Generate product ID
        String productId = RandomStringUtils.randomAlphanumeric(8);

        ProductEntry entry = new ProductEntry();
        entry.setProductId(productId);
        entry.setName(name);
        entry.setPrice(price);

        productRepository.save(entry);
        Activity activity = new Activity();
        activity.setAction("ADD_PRODUCT");
        activity.setIdentifier("productId="+productId);
        activity.setDetails("");
        submitActivity(activity);
        // recordActivity("ADD_PRODUCT", "productId="+productId, "");
        return productId;

    }

    public Product getProduct(String productId) throws ProductNotFoundException, JsonProcessingException {

        Optional<ProductEntry> entry = productRepository.findById(productId);
        if (entry.isEmpty()) {
            throw new ProductNotFoundException("Product with id {} not found", productId);
        }

        Product product = new Product();
        product.setName(entry.get().getName());
        product.setPrice(entry.get().getPrice());

        Activity activity = new Activity();
        activity.setAction("GET_PRODUCT");
        activity.setIdentifier("productId="+productId);
        activity.setDetails("");
        submitActivity(activity);
        // recordActivity("GET_PRODUCT", "productId="+productId, "");
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

    public void submitActivity(Activity activity) throws JsonProcessingException {
        String message = OBJECT_MAPPER.writeValueAsString(activity);

        SendMessageRequest sendMessageRequest = new SendMessageRequest(sqsProperties.getQueueUrl(), message);
        amazonSQS.sendMessage(sendMessageRequest);
    }

}
