package be.nicholasmeyers.skodagoogleactions.service;

import be.nicholasmeyers.skodagoogleactions.resource.request.InputRequestResource;
import be.nicholasmeyers.skodagoogleactions.resource.response.HookWebResponseResource;
import be.nicholasmeyers.skodagoogleactions.resource.response.sync.DeviceInfoSyncResource;
import be.nicholasmeyers.skodagoogleactions.resource.response.sync.DeviceSyncResource;
import be.nicholasmeyers.skodagoogleactions.resource.response.sync.NameSyncResource;
import be.nicholasmeyers.skodagoogleactions.resource.response.sync.PayloadSyncResource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static be.nicholasmeyers.skodagoogleactions.device.DeviceConfig.AIRCO;
import static be.nicholasmeyers.skodagoogleactions.device.DeviceConfig.BUTTON_HONK_HORN;
import static be.nicholasmeyers.skodagoogleactions.device.DeviceConfig.BUTTON_LIGHT_FLASH;
import static be.nicholasmeyers.skodagoogleactions.device.DeviceConfig.KILOMETER_SENSOR;

@Service("action.devices.SYNC")
public class SyncService implements WebhookService {

    @Override
    public HookWebResponseResource handleAction(InputRequestResource resource) {
        PayloadSyncResource payloadSyncResource = new PayloadSyncResource();
        payloadSyncResource.setAgentUserId("");
        payloadSyncResource.setDevices(createDevices());
        return new HookWebResponseResource("", payloadSyncResource);
    }

    private List<DeviceSyncResource> createDevices() {
        List<DeviceSyncResource> deviceSyncResources = new ArrayList<>();
        deviceSyncResources.add(createButtonDevice(BUTTON_LIGHT_FLASH, "knipperlicht"));
        deviceSyncResources.add(createButtonDevice(BUTTON_HONK_HORN, "toeter"));
        deviceSyncResources.add(createAirCooler());
        deviceSyncResources.add(createKilometerSensor());

        return deviceSyncResources;
    }

    private DeviceSyncResource createButtonDevice(String id, String name) {
        NameSyncResource nameSyncResource = createNameSyncResource(name);
        String[] traits = {"action.devices.traits.OnOff"};

        DeviceInfoSyncResource deviceInfoSyncResource = createDeviceInfoSyncResource();
        return createDeviceSyncResource(id, "action.devices.types.SWITCH", traits, nameSyncResource, deviceInfoSyncResource);
    }

    private DeviceSyncResource createAirCooler() {
        String name = "Airco";
        NameSyncResource nameSyncResource = createNameSyncResource(name);
        String[] traits = {"action.devices.traits.OnOff"};

        DeviceInfoSyncResource deviceInfoSyncResource = createDeviceInfoSyncResource();
        return createDeviceSyncResource(AIRCO, "action.devices.types.AIRCOOLER", traits, nameSyncResource, deviceInfoSyncResource);
    }

    private DeviceSyncResource createKilometerSensor() {
        String name = "Skoda";
        NameSyncResource nameSyncResource = createNameSyncResource(name);
        String[] traits = {"action.devices.traits.EnergyStorage"};

        DeviceInfoSyncResource deviceInfoSyncResource = createDeviceInfoSyncResource();
        return createDeviceSyncResource(KILOMETER_SENSOR, "action.devices.types.SENSOR", traits, nameSyncResource, deviceInfoSyncResource);
    }

    private NameSyncResource createNameSyncResource(String value) {
        String[] defaultNames = new String[1];
        defaultNames[0] = value;
        String[] nicknames = new String[1];
        nicknames[0] = value;

        return new NameSyncResource(defaultNames, value, nicknames);
    }

    private DeviceInfoSyncResource createDeviceInfoSyncResource() {
        return new DeviceInfoSyncResource("skoda-smart-home-inc", "hs1234", "3.2", "11.4");
    }

    private DeviceSyncResource createDeviceSyncResource(String id, String type, String[] traits, NameSyncResource name,
                                                        DeviceInfoSyncResource deviceInfoSyncResource) {
        return new DeviceSyncResource(id, type, traits, name, true, "Auto", deviceInfoSyncResource);
    }
}
