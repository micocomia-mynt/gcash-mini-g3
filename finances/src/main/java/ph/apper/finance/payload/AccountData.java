package ph.apper.finance.payload;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AccountData {
    @JsonProperty(value = "first_name")
    private String firstName;

    @JsonProperty(value = "last_name")
    private String lastName;

    private String email;

    private float balance;

    @JsonProperty(value = "logged_in")
    private boolean loggedIn;
}