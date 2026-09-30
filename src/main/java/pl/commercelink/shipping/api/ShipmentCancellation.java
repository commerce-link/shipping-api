package pl.commercelink.shipping.api;

import java.util.List;

/**
 * Outcome of a shipment cancellation at the provider. Cancelling is asynchronous at Furgonetka: the request only
 * returns a command id, the result is read later with {@link ShippingProvider#checkShipmentCancellation}.
 */
public record ShipmentCancellation(
        String commandId,
        Status status,
        String error,
        List<String> otherCancelledPackageIds
) {

    public enum Status {
        PENDING,
        SUCCEEDED,
        FAILED
    }

    public ShipmentCancellation {
        otherCancelledPackageIds = otherCancelledPackageIds == null ? List.of() : List.copyOf(otherCancelledPackageIds);
    }

    public static ShipmentCancellation pending(String commandId) {
        return new ShipmentCancellation(commandId, Status.PENDING, null, List.of());
    }

    public static ShipmentCancellation succeeded(String commandId, List<String> otherCancelledPackageIds) {
        return new ShipmentCancellation(commandId, Status.SUCCEEDED, null, otherCancelledPackageIds);
    }

    public static ShipmentCancellation failed(String commandId, String error, List<String> otherCancelledPackageIds) {
        return new ShipmentCancellation(commandId, Status.FAILED, error, otherCancelledPackageIds);
    }
}
