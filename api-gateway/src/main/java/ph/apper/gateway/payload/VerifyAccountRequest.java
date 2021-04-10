package ph.apper.gateway.payload;

import lombok.Data;

@Data
public class VerifyAccountRequest {
    private String verificationCode;
    private String email;
}
