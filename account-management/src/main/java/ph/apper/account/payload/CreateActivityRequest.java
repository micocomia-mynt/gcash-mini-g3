package ph.apper.account.payload;

import lombok.Data;

@Data
public class CreateActivityRequest {
    private String action;
    private String identifier;
    private String details;
}
