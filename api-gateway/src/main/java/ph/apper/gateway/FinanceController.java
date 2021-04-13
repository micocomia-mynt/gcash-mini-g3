package ph.apper.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ph.apper.gateway.payload.AddMoneyRequest;
import ph.apper.gateway.payload.TransferRequest;

@RestController
@RequestMapping
public class FinanceController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FinanceController.class);
    private final App.GCashMiniProperties gCashMiniProperties;
    @Autowired
    private final RestTemplate restTemplate;

    public FinanceController(App.GCashMiniProperties gCashMiniProperties, RestTemplate restTemplate) {
        this.gCashMiniProperties = gCashMiniProperties;
        this.restTemplate = restTemplate;
    }


    @PostMapping(path = "/transfer")
    public ResponseEntity transfer(@RequestBody TransferRequest request) {

        LOGGER.info("Transfer Request: " + request);
        try {
            ResponseEntity<Object> response = restTemplate.postForEntity(gCashMiniProperties.getFinancialUrl() + "/transfer",
                    request,
                    Object.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                LOGGER.info("Transfer successful.");
            } else {
                LOGGER.info("Err: " + response.getStatusCode());
            }
            return response;

        } catch( Exception err){
            LOGGER.error("Transfer method error: " + err);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(path = "/add")
    public ResponseEntity addMoney(@RequestBody AddMoneyRequest request) {

        LOGGER.info("Add money request: " + request);
        try {
            ResponseEntity<Object> response = restTemplate.postForEntity(gCashMiniProperties.getFinancialUrl() + "/add",
                    request,
                    Object.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                LOGGER.info("Add money successful.");
            } else {
                LOGGER.info("Err: " + response.getStatusCode());
            }

            return response;
        } catch( Exception err){

            LOGGER.error("addMoney method error: " + err);
            return ResponseEntity.badRequest().build();
        }
    }
}
