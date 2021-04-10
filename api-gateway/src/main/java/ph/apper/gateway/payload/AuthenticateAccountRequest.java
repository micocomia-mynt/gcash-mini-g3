package ph.apper.gateway.payload;

import lombok.Data;

@Data
public class AuthenticateAccountRequest {
    private String email;
    private String password;
}
