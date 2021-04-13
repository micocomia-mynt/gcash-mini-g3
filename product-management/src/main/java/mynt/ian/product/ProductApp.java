package mynt.ian.product;

import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ProductApp {

    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        SpringApplication app = new SpringApplication(ProductApp.class);
        app.addListeners(new ApplicationPidFileWriter("product_process.pid"));
        app.run(args);
    }

    @Bean
    public RestTemplate restTemplate() {

        return new RestTemplate();

    }

    @Data
    @ConfigurationProperties("sqs")
    public static class SqsProperties {
        private String queueUrl;
    }

}
