package be.nicholasmeyers.skodagoogleactions.resource.request;

import java.util.List;

public record PayloadRequestResource(List<CommandRequestResource> commands, List<DeviceRequestResource> devices) {
}
