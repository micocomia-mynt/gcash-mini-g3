package mynt.ian.product.payload;

import lombok.Data;

@Data
public class UpdateAccountRequest{

    private float balance;
    private String password;

}