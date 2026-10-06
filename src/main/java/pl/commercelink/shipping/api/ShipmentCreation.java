package pl.commercelink.shipping.api;

/**
 * Outcome of a shipment creation command. The caller picks the command id; the provider answers PENDING (usually with
 * the externalId of the created package already known) and the result is read with
 * {@link ShippingProvider#checkShipmentCreation}.
 */
public record ShipmentCreation(
        String commandId,
        CommandStatus status,
        String externalId,
        ShipmentResult result,
        String error
) {

    public static ShipmentCreation pending(String commandId, String externalId) {
        return new ShipmentCreation(commandId, CommandStatus.PENDING, externalId, null, null);
    }

    public static ShipmentCreation succeeded(String commandId, ShipmentResult result) {
        if (result == null || result.parcels().isEmpty()) {
            throw new IllegalArgumentException("A created shipment has at least one parcel");
        }
        return new ShipmentCreation(commandId, CommandStatus.SUCCEEDED, result.externalId(), result, null);
    }

    public static ShipmentCreation failed(String commandId, String externalId, String error) {
        return new ShipmentCreation(commandId, CommandStatus.FAILED, externalId, null, error);
    }
}
