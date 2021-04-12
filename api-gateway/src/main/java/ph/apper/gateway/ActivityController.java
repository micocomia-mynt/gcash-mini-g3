package ph.apper.gateway;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ph.apper.gateway.payload.Activity;
import ph.apper.gateway.payload.VerifyAccountRequest;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("activity")
public class ActivityController {

    private final RestTemplate restTemplate;
    private final App.GCashMiniProperties gCashMiniProperties;

    public ActivityController(RestTemplate restTemplate, App.GCashMiniProperties gCashMiniProperties) {
        this.restTemplate = restTemplate;
        this.gCashMiniProperties = gCashMiniProperties;
    }

    @GetMapping
    public ResponseEntity<List<Activity>> getAll() {
        ResponseEntity<Activity[]> response = restTemplate.getForEntity(gCashMiniProperties.getActivityUrl(), Activity[].class);

        if (response.getStatusCode().is2xxSuccessful()) {
            List<Activity> activities = Arrays.asList(response.getBody());
            return ResponseEntity.ok(activities);
        }

        return ResponseEntity.status(response.getStatusCode()).build();
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody Activity request){
        ResponseEntity<Void> response = restTemplate.postForEntity(gCashMiniProperties.getActivityUrl(), request, null);

        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(response.getBody());
        }

        return ResponseEntity.status(response.getStatusCode()).build();
    }

}
