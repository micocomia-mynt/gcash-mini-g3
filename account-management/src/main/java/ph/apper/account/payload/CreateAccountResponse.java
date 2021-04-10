package ph.apper.account.payload;

import lombok.Data;

@Data
public class CreateAccountResponse {
    private String verificationCode;
}
