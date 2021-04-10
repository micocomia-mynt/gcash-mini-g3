package ph.apper.account.payload;

import lombok.Data;

@Data
public class VerifyAccountRequest {
    private String verificationCode;
    private String email;
}
