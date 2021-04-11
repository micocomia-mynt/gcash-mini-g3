package mynt.ian.product.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PurchaseProduct {

    // Purchase Product JSON Request Body

    @JsonProperty(value = "productId")
    private String productId;

    @JsonProperty(value = "accountId")
    private String accountId;

}
