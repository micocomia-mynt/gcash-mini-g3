package ph.apper.account.payload;

import lombok.Data;

@Data
public class CreateAccountRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
