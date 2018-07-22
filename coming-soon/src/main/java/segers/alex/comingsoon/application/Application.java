package segers.alex.comingsoon.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@ComponentScan("segers.alex.comingsoon")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
