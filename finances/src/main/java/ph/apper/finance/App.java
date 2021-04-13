package ph.apper.finance;


import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;


@SpringBootApplication
@ConfigurationPropertiesScan
public class App {
    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        SpringApplication app = new SpringApplication(App.class);
        app.addListeners(new ApplicationPidFileWriter("finance_process.pid"));
        app.run(args);
//        SpringApplication.run(App.class);
    }

    @Data
    @ConfigurationProperties("sqs")
    public static class SqsProperties {
        private String queueUrl;
    }

    @Data
    @ConfigurationProperties(prefix = "gcash.mini")
    public static class GCashMiniProperties {
        private String activityUrl;
        private String accountUrl;
        private  String financialUrl;
    }

}





