package mynt.ian.product;

import mynt.ian.product.payload.Product;
import mynt.ian.product.payload.ProductEntry;
import mynt.ian.product.repository.ProductRepository;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public String addProduct(Product product) {

        // Generate product ID
        String productId = RandomStringUtils.randomAlphanumeric(8);

        ProductEntry entry = new ProductEntry();
        entry.setProductId(productId);
        entry.setName(product.getName());
        entry.setPrice(product.getPrice());

        productRepository.save(entry);
        return productId;

    }

    public Product getProduct(String productId) {

        ProductEntry entry = productRepository.findById(productId).get();
        Product product = new Product();
        product.setName(entry.getName());
        product.setPrice(entry.getPrice());

        return product;
    }

}
