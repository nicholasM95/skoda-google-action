package be.nicholasmeyers.skodagoogleactions;

import be.nicholasmeyers.skodagoogleactions.config.SkodaConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(SkodaConfig.class)
public class SkodaGoogleActionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkodaGoogleActionsApplication.class, args);
    }

}
