package be.nicholasmeyers.skodagoogleactions.resource.response.execute;

import java.util.List;
import java.util.UUID;

public record CommandExecuteResource(List<UUID> ids, String status, StateExecuteResource states) {
}
