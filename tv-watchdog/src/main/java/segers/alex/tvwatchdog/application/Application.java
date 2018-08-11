package segers.alex.tvwatchdog.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("segers.alex.tvwatchdog")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
