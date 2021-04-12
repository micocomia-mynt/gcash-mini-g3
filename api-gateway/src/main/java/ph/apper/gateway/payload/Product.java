package ph.apper.gateway.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Product {

    @JsonProperty(value = "name")
    private String name;

    @JsonProperty(value = "price")
    private float price;

}
