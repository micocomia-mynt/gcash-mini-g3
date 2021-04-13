package ph.apper.activity;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(App.class);
        application.addListeners(new ApplicationPidFileWriter("activity_process.pid"));
        application.run(args);
    }

    @Bean
    public CommandLineRunner pollSqs(@Value("${sqs.queueUrl}") String sqsUrl, AmazonSQS amazonSQS){
        return args -> {
            while(true){
                ReceiveMessageRequest request = new ReceiveMessageRequest(sqsUrl);
                request.withWaitTimeSeconds(5);

                ReceiveMessageResult receiveMessageResult = amazonSQS.receiveMessage(request);
                receiveMessageResult.getMessages().forEach(message -> {
                    System.out.println("Activity: " + message.getBody());
                    amazonSQS.deleteMessage(new DeleteMessageRequest(sqsUrl, message.getReceiptHandle()));
                });
            }
        };
    }

    @Configuration
    public static class SqsConfiguration{
        @Bean
        public AmazonSQS amazonSQS(){
            return AmazonSQSClientBuilder
                    .standard()
                    .withRegion(Regions.AP_SOUTH_1)
                    .build();
        }
    }

}
