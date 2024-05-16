package be.nicholasmeyers.skodagoogleactions.resource.request;

import java.util.List;

public record HookRequestResource(List<InputRequestResource> inputs, String requestId) {
}
