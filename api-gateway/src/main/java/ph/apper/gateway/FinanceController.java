package ph.apper.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import ph.apper.gateway.payload.AddMoneyRequest;
import ph.apper.gateway.payload.TransferRequest;

import java.util.Map;

public class FinanceController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FinanceController.class);

    @Autowired
    private final RestTemplate restTemplate;

    public FinanceController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @PostMapping(path = "/transfer")
    public ResponseEntity transfer(@RequestBody TransferRequest request) {

        LOGGER.info("Transfer Request: " + request);
        try {
            ResponseEntity<Object> response = restTemplate.postForEntity("http://localhost:8082/finance/transfer",
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
            ResponseEntity<Object> response = restTemplate.postForEntity("http://localhost:8082/finance/add",
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
