package pl.commercelink.shipping.api;

import java.util.List;
import java.util.Set;

public interface ShippingProvider {

    List<Carrier> getAvailableCarriers();

    List<ShippingEstimate> estimateShipment(ShipmentRequest request, Set<String> carrierIds);

    ShipmentResult createShipment(ShipmentRequest request);

    /**
     * Requests the cancellation of one shipment under a command id chosen by the caller.
     * The provider uses {@code commandId} as the idempotency key of the request, so the caller can record it
     * before sending and a repeated request with the same id never starts a second command.
     * The result is usually PENDING and must be read with {@link #checkShipmentCancellation(String, String)}.
     */
    ShipmentCancellation cancelShipment(String externalId, String commandId);

    default ShipmentCancellation checkShipmentCancellation(String commandId, String externalId) {
        throw new UnsupportedOperationException("Cancellation check is not supported by this provider");
    }

    default boolean supportsParcelTracking() {
        return false;
    }

    default ParcelTrackingSubscription trackParcel(ParcelTrackingRequest request) {
        throw new UnsupportedOperationException("Parcel tracking is not supported by this provider");
    }

    default ParcelTrackingSubscription checkParcelTracking(String subscriptionId) {
        throw new UnsupportedOperationException("Parcel tracking is not supported by this provider");
    }

    default List<TrackingEvent> getTrackingEvents(String externalId) {
        throw new UnsupportedOperationException("Parcel tracking is not supported by this provider");
    }
}
