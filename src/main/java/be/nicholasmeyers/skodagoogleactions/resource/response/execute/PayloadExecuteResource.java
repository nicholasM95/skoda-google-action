package be.nicholasmeyers.skodagoogleactions.resource.response.execute;

import be.nicholasmeyers.skodagoogleactions.resource.response.PayloadWebResponseResource;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PayloadExecuteResource extends PayloadWebResponseResource {
    private List<CommandExecuteResource> commands;
}
