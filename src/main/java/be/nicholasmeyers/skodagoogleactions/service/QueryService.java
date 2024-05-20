package be.nicholasmeyers.skodagoogleactions.service;

import be.nicholasmeyers.skodagoogleactions.client.CoolingClient;
import be.nicholasmeyers.skodagoogleactions.client.StatusClient;
import be.nicholasmeyers.skodagoogleactions.client.resource.CoolingWebResponseResource;
import be.nicholasmeyers.skodagoogleactions.client.resource.DataWebResponseResource;
import be.nicholasmeyers.skodagoogleactions.client.resource.FieldWebResponseResource;
import be.nicholasmeyers.skodagoogleactions.client.resource.StatusWebResponseResource;
import be.nicholasmeyers.skodagoogleactions.config.SkodaConfig;
import be.nicholasmeyers.skodagoogleactions.exception.KilometerException;
import be.nicholasmeyers.skodagoogleactions.exception.WebHookInputException;
import be.nicholasmeyers.skodagoogleactions.resource.request.DeviceRequestResource;
import be.nicholasmeyers.skodagoogleactions.resource.request.HookRequestResource;
import be.nicholasmeyers.skodagoogleactions.resource.request.InputRequestResource;
import be.nicholasmeyers.skodagoogleactions.resource.response.HookWebResponseResource;
import be.nicholasmeyers.skodagoogleactions.resource.response.query.KilometerQueryResource;
import be.nicholasmeyers.skodagoogleactions.resource.response.query.PayloadQueryResource;
import be.nicholasmeyers.skodagoogleactions.resource.response.query.StateQueryResource;
import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service("action.devices.QUERY")
public class QueryService implements WebhookService {

    private final CoolingClient coolingClient;
    private final StatusClient statusClient;
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
        setSentryTag(devicesId);
        Map<UUID, StateQueryResource> devices = new HashMap<>();
        devicesId.forEach(id -> {
            if ("6abb7eaa-08a8-44c0-83a7-9c3c658bd63e".equals(id.toString())) {
                devices.put(id, new StateQueryResource("SUCCESS", true, false, null));
            } else if ("883f8b70-1649-41f2-8a53-b41df7214f4a".equals(id.toString())) {
                devices.put(id, new StateQueryResource("SUCCESS", true, false, null));
            } else if ("b1c18c45-8e42-493c-a3c0-928bd631caf7".equals(id.toString())) {
                devices.put(id, new StateQueryResource("SUCCESS", true, isAirCoolerOn(), null));
            } else if ("2ec009da-cd6f-4adc-9021-9e7861358408".equals(id.toString())) {
                KilometerQueryResource kilometerQueryResource = new KilometerQueryResource("KILOMETERS", getKilometers());
                devices.put(id, new StateQueryResource("SUCCESS", true, false, Collections.singletonList(kilometerQueryResource)));
            }
        });
        return devices;
    }

    private int getKilometers() {
        ResponseEntity<StatusWebResponseResource> status = statusClient.getStatus(skodaConfig.getVin());
        if (status == null || !status.getStatusCode().is2xxSuccessful() || status.getBody() == null) {
            throw new KilometerException("Status is empty");
        }

        List<DataWebResponseResource> data = status.getBody().getData().stream()
                .filter(dataWebResponseResource -> "0x030103FFFF".equals(dataWebResponseResource.getId()))
                .toList();

        if (data.isEmpty()) {
            throw new KilometerException("Data is not complete");
        }

        List<FieldWebResponseResource> fields = data.stream().findFirst().get().getFields().stream()
                .filter(fieldWebResponseResource -> "0x0301030006".equals(fieldWebResponseResource.getId()))
                .filter(fieldWebResponseResource -> canBeParsedToInt(fieldWebResponseResource.getValue()))
                .toList();

        if (fields.isEmpty()) {
            throw new KilometerException("Fields are not complete");
        }
        return Integer.parseInt(fields.stream().findFirst().get().getValue());
    }

    private boolean isAirCoolerOn() {
        ResponseEntity<CoolingWebResponseResource> cooling = coolingClient.getCoolingStatus(skodaConfig.getVin());
        if (cooling != null && cooling.getStatusCode().is2xxSuccessful() && cooling.getBody() != null
                && cooling.getBody().getReport() != null && cooling.getBody().getReport().getRemainingClimateTime() != null) {
            return cooling.getBody().getReport().getRemainingClimateTime() != 0;
        }
        return false;
    }

    private static boolean canBeParsedToInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void setSentryTag(List<UUID> devicesId) {
        List<String> devices = devicesId.stream().map(UUID::toString).toList();
        Sentry.setTag("device", String.join(",", devices));
    }
}
