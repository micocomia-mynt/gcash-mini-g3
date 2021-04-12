package ph.apper.finance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        SpringApplication app = new SpringApplication(App.class);
        app.addListeners(new ApplicationPidFileWriter("finance_process.pid"));
        app.run(args);
//        SpringApplication.run(App.class);
    }
}





