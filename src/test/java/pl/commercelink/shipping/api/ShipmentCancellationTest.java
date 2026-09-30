package pl.commercelink.shipping.api;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipmentCancellationTest {

    @Test
    void pendingCarriesCommandIdOnly() {
        // when
        ShipmentCancellation cancellation = ShipmentCancellation.pending("cmd-1");

        // then
        assertEquals("cmd-1", cancellation.commandId());
        assertEquals(ShipmentCancellation.Status.PENDING, cancellation.status());
        assertNull(cancellation.error());
        assertTrue(cancellation.otherCancelledPackageIds().isEmpty());
    }

    @Test
    void failedCarriesErrorAndOtherPackages() {
        // when
        ShipmentCancellation cancellation = ShipmentCancellation.failed("cmd-1", "already picked up", List.of("21353833"));

        // then
        assertEquals(ShipmentCancellation.Status.FAILED, cancellation.status());
        assertEquals("already picked up", cancellation.error());
        assertEquals(List.of("21353833"), cancellation.otherCancelledPackageIds());
    }

    @Test
    void otherCancelledPackageIdsAreNeverNullAndImmutable() {
        // when
        ShipmentCancellation cancellation = ShipmentCancellation.succeeded("cmd-1", null);

        // then
        assertTrue(cancellation.otherCancelledPackageIds().isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> cancellation.otherCancelledPackageIds().add("x"));
    }

    @Test
    void providerWithoutCancellationCheckRejectsTheCheck() {
        // given
        ShippingProvider provider = new ShippingProvider() {
            public List<Carrier> getAvailableCarriers() { return List.of(); }
            public List<ShippingEstimate> estimateShipment(ShipmentRequest request, java.util.Set<String> carrierIds) { return List.of(); }
            public ShipmentResult createShipment(ShipmentRequest request) { return null; }
            public ShipmentCancellation cancelShipment(String externalId, String commandId) { return ShipmentCancellation.pending(commandId); }
        };

        // when / then
        assertThrows(UnsupportedOperationException.class, () -> provider.checkShipmentCancellation("cmd-1", "PKG-1"));
    }
}
