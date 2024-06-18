package be.nicholasmeyers.skodagoogleactions.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "ventilator-client", url = "http://api-skoda.skoda.svc.cluster.local:8080", configuration = ClientConfig.class)
public interface VentilatorClient extends VentilatorApi {
}
