package be.nicholasmeyers.skodagoogleactions.config;

import be.nicholasmeyers.skodaconnector.service.ConnectorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SkodaConnectorConfig {
    @Bean
    public ConnectorService connectorService() {
        return new ConnectorService();
    }
}
