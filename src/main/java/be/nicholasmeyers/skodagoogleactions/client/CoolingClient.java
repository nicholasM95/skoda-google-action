package be.nicholasmeyers.skodagoogleactions.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "cooling-client", url = "https://api-skoda.nicholasmeyers.be", configuration = ClientConfig.class)
public interface CoolingClient extends CoolingApi {
}
