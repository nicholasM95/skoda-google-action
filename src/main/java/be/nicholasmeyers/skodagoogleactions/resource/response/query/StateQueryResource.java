package be.nicholasmeyers.skodagoogleactions.resource.response.query;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StateQueryResource(String status, boolean online, boolean on, List<KilometerQueryResource> capacityRemaining) {
}
