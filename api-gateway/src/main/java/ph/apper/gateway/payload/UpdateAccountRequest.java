package ph.apper.gateway.payload;

import lombok.Data;

@Data
public class UpdateAccountRequest {
    private float balance;
    private String password;
}