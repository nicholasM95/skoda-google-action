package be.nicholasmeyers.skodagoogleactions;

import be.nicholasmeyers.skodagoogleactions.client.*;
import be.nicholasmeyers.skodagoogleactions.config.SkodaConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableConfigurationProperties(SkodaConfig.class)
@EnableFeignClients(clients = {CoolingClient.class, FlashClient.class, HonkClient.class,
        LocationClient.class, RequestClient.class, StatusClient.class, VentilatorClient.class})
public class SkodaGoogleActionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkodaGoogleActionsApplication.class, args);
    }

}
