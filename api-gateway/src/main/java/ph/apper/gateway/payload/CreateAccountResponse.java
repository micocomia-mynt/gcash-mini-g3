package ph.apper.gateway.payload;

import lombok.Data;

@Data
public class CreateAccountResponse {
    private String verificationCode;
}
