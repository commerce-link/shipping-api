package pl.commercelink.shipping.api;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface ShippingProvider {

    List<Carrier> getAvailableCarriers();

    List<ShippingEstimate> estimateShipment(ShipmentRequest request, Set<String> carrierIds);

    /**
     * Starts creating a shipment under a command id chosen by the caller. Usually PENDING: the result is read with
     * {@link #checkShipmentCreation}. Never orders a courier pickup: that is {@link #orderPickup}.
     */
    ShipmentCreation createShipment(ShipmentRequest request, String commandId);

    /** Reads the creation command; externalId may be null when the start call ended without an answer. */
    ShipmentCreation checkShipmentCreation(String commandId, String externalId);

    default boolean supportsPickups() {
        return false;
    }

    /** Pickup windows common to all given packages, sorted, available ones only. */
    default List<PickupWindow> pickupWindows(List<String> externalIds, LocalDate readyDate, int daysAhead) {
        throw new UnsupportedOperationException("Courier pickups are not supported by this provider");
    }

    /** Orders one courier pickup for all given packages in the window, under a command id chosen by the caller. */
    default PickupOrder orderPickup(List<String> externalIds, PickupWindow window, String commandId) {
        throw new UnsupportedOperationException("Courier pickups are not supported by this provider");
    }

    default PickupOrder checkPickupOrder(String commandId) {
        throw new UnsupportedOperationException("Courier pickups are not supported by this provider");
    }

    default boolean supportsLabels() {
        return false;
    }

    default Label getLabel(String externalId) {
        throw new UnsupportedOperationException("Labels are not supported by this provider");
    }

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
