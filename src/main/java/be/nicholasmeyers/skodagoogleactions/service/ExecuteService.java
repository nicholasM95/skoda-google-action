package be.nicholasmeyers.skodagoogleactions.service;

import be.nicholasmeyers.skodagoogleactions.client.FlashClient;
import be.nicholasmeyers.skodagoogleactions.client.LocationClient;
import be.nicholasmeyers.skodagoogleactions.client.RequestClient;
import be.nicholasmeyers.skodagoogleactions.client.VentilatorClient;
import be.nicholasmeyers.skodagoogleactions.client.resource.*;
import be.nicholasmeyers.skodagoogleactions.config.SkodaConfig;
import be.nicholasmeyers.skodagoogleactions.exception.CommandRequestException;
import be.nicholasmeyers.skodagoogleactions.exception.FlashException;
import be.nicholasmeyers.skodagoogleactions.exception.LocationException;
import be.nicholasmeyers.skodagoogleactions.exception.WebHookInputException;
import be.nicholasmeyers.skodagoogleactions.resource.request.CommandRequestResource;
import be.nicholasmeyers.skodagoogleactions.resource.request.InputRequestResource;
import be.nicholasmeyers.skodagoogleactions.resource.response.HookWebResponseResource;
import be.nicholasmeyers.skodagoogleactions.resource.response.execute.CommandExecuteResource;
import be.nicholasmeyers.skodagoogleactions.resource.response.execute.PayloadExecuteResource;
import be.nicholasmeyers.skodagoogleactions.resource.response.execute.StateExecuteResource;
import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service("action.devices.EXECUTE")
public class ExecuteService implements WebhookService {

    private final FlashClient flashClient;
    private final LocationClient locationClient;
    private final RequestClient requestClient;
    private final VentilatorClient ventilatorClient;
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
        setSentryTag(device);
        return Collections.singletonList(commandExecuteResource);
    }

    private String handleCommand(UUID device, boolean on) {
        if (on) {
            if (UUID.fromString("6abb7eaa-08a8-44c0-83a7-9c3c658bd63e").equals(device)) {
                // Flash
                LocationWebResponseResource location = getLocation();
                FlashWebResponseResource flash = flash(location.getLatitude(), location.getLongitude());
                if ("REQUEST_IN_PROGRESS".equals(flash.getStatus())) {
                    return "SUCCESS";
                }
            } else if (UUID.fromString("883f8b70-1649-41f2-8a53-b41df7214f4a").equals(device)) {
                // Honk
            } else if (UUID.fromString("b1c18c45-8e42-493c-a3c0-928bd631caf7").equals(device)) {
                // Start Ventilator
                VentilatorWebRequestResource ventilatorWebRequestResource = new VentilatorWebRequestResource(30, skodaConfig.getPin());
                ResponseEntity<VentilatorWebResponseResource> ventilatorWebResponseResource = ventilatorClient.startVentilator(skodaConfig.getVin(), ventilatorWebRequestResource);
                return handleVentilatorRequest(ventilatorWebResponseResource);
            }
        } else if (UUID.fromString("b1c18c45-8e42-493c-a3c0-928bd631caf7").equals(device)) {
            // Stop Ventilator
            VentilatorWebRequestResource ventilatorWebRequestResource = new VentilatorWebRequestResource(0, skodaConfig.getPin());
            ResponseEntity<VentilatorWebResponseResource> ventilatorWebResponseResource = ventilatorClient.stopVentilator(skodaConfig.getVin(), ventilatorWebRequestResource);
            return handleVentilatorRequest(ventilatorWebResponseResource);
        }
        return "FAILURE";
    }

    private boolean isOn(UUID device, String status, boolean on) {
        if (UUID.fromString("b1c18c45-8e42-493c-a3c0-928bd631caf7").equals(device)) {
            return "SUCCESS".equals(status) && on;
        }
        return false;
    }

    private LocationWebResponseResource getLocation() {
        ResponseEntity<LocationWebResponseResource> location = locationClient.getLocation(skodaConfig.getVin());
        if (location != null && location.getStatusCode().is2xxSuccessful() && location.getBody() != null
                && location.getBody().getLatitude() != null && location.getBody().getLongitude() != null) {
            return location.getBody();
        }
        throw new LocationException("Can't find location");
    }

    private FlashWebResponseResource flash(int latitude, int longitude) {
        FlashWebRequestResource flashWebRequestResource = new FlashWebRequestResource(latitude, longitude, 30);
        ResponseEntity<FlashWebResponseResource> flash = flashClient.flash(skodaConfig.getVin(), flashWebRequestResource);
        if (flash != null && flash.getStatusCode().is2xxSuccessful() && flash.getBody() != null) {
            return flash.getBody();
        }
        throw new FlashException("Can't flash lights");
    }

    private String handleVentilatorRequest(ResponseEntity<VentilatorWebResponseResource> ventilatorWebResponseResource) {
        if (ventilatorWebResponseResource != null && ventilatorWebResponseResource.getStatusCode().is2xxSuccessful() &&
                ventilatorWebResponseResource.getBody() != null) {
            String id = ventilatorWebResponseResource.getBody().getId();
            for (int i = 0; i < 15; i++) {
                ResponseEntity<RequestWebResponseResource> request = requestClient.getRequest(skodaConfig.getVin(), id);
                if (request != null && request.getStatusCode().is2xxSuccessful() && request.getBody() != null) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if ("request_successful".equals(request.getBody().getStatus())) {
                        return "SUCCESS";
                    }
                }
            }
        }
        return "FAILURE";
    }

    private void setSentryTag(UUID deviceId) {
        Sentry.setTag("action_device", deviceId.toString());
    }
}
