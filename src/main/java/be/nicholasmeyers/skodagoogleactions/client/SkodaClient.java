package be.nicholasmeyers.skodagoogleactions.client;

import be.nicholasmeyers.skoda.api.client.VehicleService;
import be.nicholasmeyers.skodagoogleactions.config.SkodaConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SkodaClient {

    private final SkodaConfig skodaConfig;

    @Bean
    public VehicleService vehicleService() {
        return new VehicleService(skodaConfig.getEmail(), skodaConfig.getPassword(), skodaConfig.getApi());
    }
}
