package pl.commercelink.shipping.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParcelTrackingSubscriptionTest {

    @Test
    void activeCarriesExternalIdAndCarrier() {
        // when
        ParcelTrackingSubscription subscription = ParcelTrackingSubscription.active("cmd-1", "21037943", "dpd");

        // then
        assertEquals(ParcelTrackingSubscription.Status.ACTIVE, subscription.status());
        assertEquals("21037943", subscription.externalId());
        assertEquals("dpd", subscription.carrier());
        assertNull(subscription.error());
    }

    @Test
    void failedCarriesErrorOnly() {
        // when
        ParcelTrackingSubscription subscription = ParcelTrackingSubscription.failed("cmd-1", "carrier not recognised");

        // then
        assertEquals(ParcelTrackingSubscription.Status.FAILED, subscription.status());
        assertEquals("carrier not recognised", subscription.error());
        assertNull(subscription.externalId());
    }

    @Test
    void providerWithoutTrackingSupportRejectsTrackingCalls() {
        // given
        ShippingProvider provider = new ShippingProvider() {
            public java.util.List<Carrier> getAvailableCarriers() { return java.util.List.of(); }
            public java.util.List<ShippingEstimate> estimateShipment(ShipmentRequest request, java.util.Set<String> carrierIds) { return java.util.List.of(); }
            public ShipmentResult createShipment(ShipmentRequest request) { return null; }
            public ShipmentCancellation cancelShipment(String externalId, String commandId) { return ShipmentCancellation.pending(commandId); }
        };

        // then
        assertFalse(provider.supportsParcelTracking());
        assertThrows(UnsupportedOperationException.class,
                () -> provider.trackParcel(new ParcelTrackingRequest("X1", "DPD", "label")));
        assertThrows(UnsupportedOperationException.class, () -> provider.checkParcelTracking("cmd-1"));
        assertThrows(UnsupportedOperationException.class, () -> provider.getTrackingEvents("1"));
    }
}
