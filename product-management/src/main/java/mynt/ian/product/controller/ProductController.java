package mynt.ian.product.controller;

import mynt.ian.product.ProductService;
import mynt.ian.product.payload.Product;
import mynt.ian.product.payload.ProductResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("product")
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> addProduct(@Valid @RequestBody Product request) {
        try {
            String productId = productService.addProduct(request.getName(), request.getPrice());
            ProductResponse response = new ProductResponse();
            response.setProductId(productId);
            LOGGER.info("SUCCESS: Added product with id {}", productId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOGGER.error("ERROR: Failed to add product", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable("productId") String productId) {
        try {
            return ResponseEntity.ok(productService.getProduct(productId));
        } catch(Exception e) {
            LOGGER.error("ERROR: Product not found", e);
            return ResponseEntity.notFound().build();
        }

    }
}
