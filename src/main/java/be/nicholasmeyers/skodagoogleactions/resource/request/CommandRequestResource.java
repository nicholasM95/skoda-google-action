package be.nicholasmeyers.skodagoogleactions.resource.request;

import java.util.List;

public record CommandRequestResource(List<DeviceRequestResource> devices, List<ExecutionRequestResource> execution) {
}
