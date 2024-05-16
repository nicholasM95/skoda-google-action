package be.nicholasmeyers.skodagoogleactions.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "request-client", url = "https://api-skoda.nicholasmeyers.be", configuration = ClientConfig.class)
public interface RequestClient extends RequestApi {
}
