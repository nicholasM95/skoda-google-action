package be.nicholasmeyers.skodagoogleactions.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "skoda")
public class SkodaConfig {
    private String email;
    private String password;
    private String pin;
    private String vin;
    private String api;
}
