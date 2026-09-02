package pl.commercelink.shipping.api;

import java.util.List;
import java.util.Set;

public interface ShippingProvider {

    List<Carrier> getAvailableCarriers();

    List<ShippingEstimate> estimateShipment(ShipmentRequest request, Set<String> carrierIds);

    ShipmentResult createShipment(ShipmentRequest request);

    void cancelShipment(String externalId);

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
