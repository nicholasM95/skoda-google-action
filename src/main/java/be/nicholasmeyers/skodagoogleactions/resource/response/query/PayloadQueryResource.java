package be.nicholasmeyers.skodagoogleactions.resource.response.query;

import be.nicholasmeyers.skodagoogleactions.resource.response.PayloadWebResponseResource;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class PayloadQueryResource extends PayloadWebResponseResource {
    private Map<UUID, StateQueryResource> devices;
}
