package be.nicholasmeyers.skodagoogleactions.client;

import be.nicholasmeyers.skodagoogleactions.config.SkodaConfig;
import be.nicholasmeyers.vwgroupconnector.resource.Client;
import be.nicholasmeyers.vwgroupconnector.service.ConnectorService;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
public class ClientConfig {
    private final ConnectorService connectorService;
    private final SkodaConfig skodaConfig;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate ->  {
            String accessToken = connectorService.getTokens(Client.VWG, skodaConfig.getEmail(), skodaConfig.getPassword()).getAccessToken();
            requestTemplate.header("Authorization", "Bearer " + accessToken);
        };
    }
}
