package be.nicholasmeyers.skodagoogleactions.service;

import be.nicholasmeyers.skoda.api.client.VehicleService;
import be.nicholasmeyers.skoda.api.client.VehicleServiceException;
import be.nicholasmeyers.skodagoogleactions.config.SkodaConfig;
import be.nicholasmeyers.skodagoogleactions.exception.KilometerException;
import be.nicholasmeyers.skodagoogleactions.exception.WebHookInputException;
import be.nicholasmeyers.skodagoogleactions.resource.request.DeviceRequestResource;
import be.nicholasmeyers.skodagoogleactions.resource.request.InputRequestResource;
import be.nicholasmeyers.skodagoogleactions.resource.response.HookWebResponseResource;
import be.nicholasmeyers.skodagoogleactions.resource.response.query.KilometerQueryResource;
import be.nicholasmeyers.skodagoogleactions.resource.response.query.PayloadQueryResource;
import be.nicholasmeyers.skodagoogleactions.resource.response.query.StateQueryResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static be.nicholasmeyers.skodagoogleactions.device.DeviceConfig.AIRCO;
import static be.nicholasmeyers.skodagoogleactions.device.DeviceConfig.KILOMETER_SENSOR;

@Slf4j
@RequiredArgsConstructor
@Service("action.devices.QUERY")
public class QueryService implements WebhookService {

    private final VehicleService vehicleService;
    private final SkodaConfig skodaConfig;

    @Override
    public HookWebResponseResource handleAction(InputRequestResource resource) {
        if (resource.payload() == null || resource.payload().devices() == null || resource.payload().devices().isEmpty()) {
            throw new WebHookInputException("Invalid payload");
        }

        PayloadQueryResource payloadQueryResource = new PayloadQueryResource();
        payloadQueryResource.setAgentUserId("");
        payloadQueryResource.setDevices(createDevices(resource.payload().devices().stream().map(DeviceRequestResource::id).toList()));

        return new HookWebResponseResource("", payloadQueryResource);
    }

    private Map<UUID, StateQueryResource> createDevices(List<UUID> devicesId) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        Map<UUID, StateQueryResource> devices = new HashMap<>();
        devicesId.forEach(id -> {
            if (AIRCO.equals(id.toString())) {
                log.info("get air cooler info");
                CompletableFuture<Void> future = isAirCoolerOn().thenAcceptAsync(isOn -> {
                    log.info("get air cooler info done");
                    devices.put(id, new StateQueryResource("SUCCESS", true, isOn, null));
                });
                futures.add(future);
            } else if (KILOMETER_SENSOR.equals(id.toString())) {
                log.info("get kilometers info");
                CompletableFuture<Void> future = getKilometers().thenAcceptAsync(kilometers -> {
                    log.info("get kilometers info done");
                    KilometerQueryResource kilometerQueryResource = new KilometerQueryResource("KILOMETERS", kilometers);
                    devices.put(id, new StateQueryResource("SUCCESS", true, false, Collections.singletonList(kilometerQueryResource)));
                });
                futures.add(future);
            }
        });
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        log.info("all done");
        return devices;
    }

    private CompletableFuture<Integer> getKilometers() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Integer totalRange = vehicleService.getVehicleRange(skodaConfig.getVin()).getTotalRangeInKm();
                Integer remainingRange = vehicleService.getVehicleRange(skodaConfig.getVin()).getRemainingRangeInKm();
                log.info("Total range: {}, Remaining range: {}", totalRange, remainingRange);
                return remainingRange;
            } catch (VehicleServiceException e) {
                log.error("{} --- {}", e.getMessage(), e.getOriginalMessage());
                throw new KilometerException("Can't get kilometer information");
            }
        });
    }

    private CompletableFuture<Boolean> isAirCoolerOn() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String state = vehicleService.getVehicleAirConditioning(skodaConfig.getVin()).getState();
                return Objects.equals(state, "VENTILATION");
            } catch (VehicleServiceException e) {
                log.error("{} --- {}", e.getMessage(), e.getOriginalMessage());
                return false;
            }
        });
    }
}
