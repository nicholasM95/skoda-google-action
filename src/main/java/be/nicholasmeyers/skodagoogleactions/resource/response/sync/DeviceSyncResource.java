package be.nicholasmeyers.skodagoogleactions.resource.response.sync;

public record DeviceSyncResource(String id, String type, String[] traits, NameSyncResource name, boolean willReportState,
                                 String roomHint, DeviceInfoSyncResource deviceInfo) {
}
