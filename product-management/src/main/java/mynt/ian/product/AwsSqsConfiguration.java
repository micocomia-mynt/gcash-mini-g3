package mynt.ian.product;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsSqsConfiguration {

    @Bean
    public AmazonSQS amazonSQS() {
        return AmazonSQSClientBuilder.standard().withRegion(Regions.AP_SOUTH_1).build();
    }

}
