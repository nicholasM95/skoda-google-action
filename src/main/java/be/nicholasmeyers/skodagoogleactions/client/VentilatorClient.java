package be.nicholasmeyers.skodagoogleactions.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "ventilator-client", url = "https://api-skoda.nicholasmeyers.be", configuration = ClientConfig.class)
public interface VentilatorClient extends VentilatorApi {
}
