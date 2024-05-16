package be.nicholasmeyers.skodagoogleactions.resource.request;

import java.util.Map;

public record ExecutionRequestResource(String command, Map<String, Object> params) {
}
