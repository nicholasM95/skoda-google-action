package be.nicholasmeyers.skodagoogleactions.service;

import be.nicholasmeyers.skoda.api.client.VehicleService;
import be.nicholasmeyers.skoda.api.client.VehicleServiceException;
import be.nicholasmeyers.skodagoogleactions.config.SkodaConfig;
import be.nicholasmeyers.skodagoogleactions.exception.CommandRequestException;
import be.nicholasmeyers.skodagoogleactions.exception.WebHookInputException;
import be.nicholasmeyers.skodagoogleactions.resource.request.CommandRequestResource;
import be.nicholasmeyers.skodagoogleactions.resource.request.InputRequestResource;
import be.nicholasmeyers.skodagoogleactions.resource.response.HookWebResponseResource;
import be.nicholasmeyers.skodagoogleactions.resource.response.execute.CommandExecuteResource;
import be.nicholasmeyers.skodagoogleactions.resource.response.execute.PayloadExecuteResource;
import be.nicholasmeyers.skodagoogleactions.resource.response.execute.StateExecuteResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static be.nicholasmeyers.skoda.api.client.VehicleHeaterSource.ELECTRIC;
import static be.nicholasmeyers.skoda.api.client.VehicleTemperatureUnit.CELSIUS;
import static be.nicholasmeyers.skodagoogleactions.device.DeviceConfig.AIRCO;

@Slf4j
@RequiredArgsConstructor
@Service("action.devices.EXECUTE")
public class ExecuteService implements WebhookService {

    private final VehicleService vehicleService;
    private final SkodaConfig skodaConfig;

    @Override
    public HookWebResponseResource handleAction(InputRequestResource resource) {
        if (resource.payload() == null || resource.payload().commands() == null) {
            throw new WebHookInputException("Invalid payload");
        }

        PayloadExecuteResource payloadExecuteResource = new PayloadExecuteResource();
        payloadExecuteResource.setAgentUserId("");
        payloadExecuteResource.setCommands(createCommands(resource.payload().commands()));

        return new HookWebResponseResource("", payloadExecuteResource);
    }

    private List<CommandExecuteResource> createCommands(List<CommandRequestResource> commandsResources) {
        if (commandsResources.size() != 1) {
            throw new CommandRequestException("Invalid command size");
        }
        CommandRequestResource commandToExecute = commandsResources.stream().findFirst().get();
        if (commandToExecute.devices() == null || commandToExecute.devices().size() != 1) {
            throw new CommandRequestException("Invalid devices size");
        }
        if (commandToExecute.execution() == null || commandToExecute.execution().size() != 1) {
            throw new CommandRequestException("Invalid execution size");
        }
        UUID device = commandToExecute.devices().stream().findFirst().get().id();
        String command = commandToExecute.execution().stream().findFirst().get().command();
        Map<String, Object> action = commandToExecute.execution().stream().findFirst().get().params();

        if (action == null || !action.containsKey("on")) {
            throw new CommandRequestException("Invalid command action");
        }
        log.info("command for: {}, --- {} on: {}", device, command, action.get("on"));
        String status = handleCommand(device, (Boolean) action.get("on"));

        CommandExecuteResource commandExecuteResource = new CommandExecuteResource(Collections.singletonList(device),
                status, new StateExecuteResource(true, isOn(device, status, (Boolean) action.get("on"))));
        return Collections.singletonList(commandExecuteResource);
    }

    private String handleCommand(UUID device, boolean on) {
        if (on) {
            if (UUID.fromString(AIRCO).equals(device)) {
                // Start Ventilator
                try {
                    vehicleService.startVehicleAirConditioning(skodaConfig.getVin(),  ELECTRIC, 20, CELSIUS);
                    return handleVentilatorRequest("id");
                } catch (VehicleServiceException e) {
                    return handleVentilatorRequest(null);
                }

            }
        } else if (UUID.fromString(AIRCO).equals(device)) {
            // Stop Ventilator
            try {
                vehicleService.stopVehicleAirConditioning(skodaConfig.getVin());
                return handleVentilatorRequest("id");
            } catch (VehicleServiceException e) {
                return handleVentilatorRequest(null);
            }
        }
        return "FAILURE";
    }

    private boolean isOn(UUID device, String status, boolean on) {
        if (UUID.fromString(AIRCO).equals(device)) {
            return "SUCCESS".equals(status) && on;
        }
        return false;
    }

    private String handleVentilatorRequest(String id) {
        if (id != null) {
            return "SUCCESS";
        }
        return "FAILURE";
    }
}
