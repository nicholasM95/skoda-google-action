package be.nicholasmeyers.skodagoogleactions.resource.response.sync;

import be.nicholasmeyers.skodagoogleactions.resource.response.PayloadWebResponseResource;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PayloadSyncResource extends PayloadWebResponseResource {
    private List<DeviceSyncResource> devices;
}
