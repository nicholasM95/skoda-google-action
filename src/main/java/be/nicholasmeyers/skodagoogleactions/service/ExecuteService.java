package be.nicholasmeyers.skodagoogleactions.service;

import be.nicholasmeyers.skoda.api.client.CarService;
import be.nicholasmeyers.skoda.api.client.CarServiceException;
import be.nicholasmeyers.skodagoogleactions.config.SkodaConfig;
import be.nicholasmeyers.skodagoogleactions.exception.CommandRequestException;
import be.nicholasmeyers.skodagoogleactions.exception.FlashException;
import be.nicholasmeyers.skodagoogleactions.exception.HonkException;
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

import static be.nicholasmeyers.skodagoogleactions.device.DeviceConfig.AIRCO;
import static be.nicholasmeyers.skodagoogleactions.device.DeviceConfig.BUTTON_HONK_HORN;
import static be.nicholasmeyers.skodagoogleactions.device.DeviceConfig.BUTTON_LIGHT_FLASH;

@Slf4j
@RequiredArgsConstructor
@Service("action.devices.EXECUTE")
public class ExecuteService implements WebhookService {

    private final CarService carService;
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
            if (UUID.fromString(BUTTON_LIGHT_FLASH).equals(device)) {
                // Flash
                String flash = flash();
                if ("REQUEST_IN_PROGRESS".equals(flash)) {
                    return "SUCCESS";
                }
            } else if (UUID.fromString(BUTTON_HONK_HORN).equals(device)) {
                // Honk
                String honk = honk();
                if ("REQUEST_IN_PROGRESS".equals(honk)) {
                    return "SUCCESS";
                }
            } else if (UUID.fromString(AIRCO).equals(device)) {
                // Start Ventilator
                String id = carService.startVentilator(skodaConfig.getVin(), skodaConfig.getPin(), 30);
                return handleVentilatorRequest(id);
            }
        } else if (UUID.fromString(AIRCO).equals(device)) {
            // Stop Ventilator
            String id = carService.stopVentilator(skodaConfig.getVin(),  skodaConfig.getPin());
            return handleVentilatorRequest(id);
        }
        return "FAILURE";
    }

    private boolean isOn(UUID device, String status, boolean on) {
        if (UUID.fromString(AIRCO).equals(device)) {
            return "SUCCESS".equals(status) && on;
        }
        return false;
    }

    private String flash() {
        try {
            return carService.flash(skodaConfig.getVin(), 30);
        } catch (CarServiceException e) {
            log.error("{} --- {}", e.getMessage(), e.getOriginalMessage());
            throw new FlashException("Can't flash lights");
        }
    }

    private String honk() {
        try {
            return carService.honk(skodaConfig.getVin(), 30);
        } catch (CarServiceException e) {
            log.error("{} --- {}", e.getMessage(), e.getOriginalMessage());
            throw new HonkException("Can't honk");
        }
    }

    private String handleVentilatorRequest(String id) {
        if (id != null) {
            return "SUCCESS";
        }
        return "FAILURE";
    }
}
