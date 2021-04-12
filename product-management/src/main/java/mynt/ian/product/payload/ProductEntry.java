package mynt.ian.product.payload;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class ProductEntry {

    @Id
    private String productId;
    private String name;
    private float price;

    public ProductEntry() {}

}

