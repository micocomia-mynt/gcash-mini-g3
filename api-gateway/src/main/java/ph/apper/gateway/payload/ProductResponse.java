package ph.apper.gateway.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProductResponse {

    // Add Product JSON Response Body

    @JsonProperty(value = "productId")
    private String productId;


}
