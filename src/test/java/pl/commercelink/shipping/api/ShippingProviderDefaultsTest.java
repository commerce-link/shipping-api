package pl.commercelink.shipping.api;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ShippingProviderDefaultsTest {

    private static final ShipmentAddress PICKUP =
            new ShipmentAddress("Magazyn", "Sklep", "Prosta 1", "00-001", "Warszawa", "PL", "m@example.com", "500600700");
    private static final PickupWindow WINDOW =
            new PickupWindow(LocalDate.of(2026, 10, 9), LocalTime.of(9, 0), LocalTime.of(17, 0), "hash-1");

    /** Implements only what a 0.5.0 adapter (Furgonetka) implements. */
    private static class ProviderWrittenFor050 implements ShippingProvider {

        final List<String> calls = new ArrayList<>();

        @Override
        public List<Carrier> getAvailableCarriers() {
            return List.of();
        }

        @Override
        public List<ShippingEstimate> estimateShipment(ShipmentRequest request, Set<String> carrierIds) {
            return List.of();
        }

        @Override
        public ShipmentCreation createShipment(ShipmentRequest request, String commandId) {
            return ShipmentCreation.pending(commandId, "1");
        }

        @Override
        public ShipmentCreation checkShipmentCreation(String commandId, String externalId) {
            return ShipmentCreation.pending(commandId, externalId);
        }

        @Override
        public boolean supportsPickups() {
            return true;
        }

        @Override
        public List<PickupWindow> pickupWindows(List<String> externalIds, LocalDate readyDate, int daysAhead) {
            calls.add("pickupWindows " + externalIds + " " + readyDate + " " + daysAhead);
            return List.of(WINDOW);
        }

        @Override
        public PickupOrder orderPickup(List<String> externalIds, PickupWindow window, String commandId) {
            calls.add("orderPickup " + externalIds + " " + window.token() + " " + commandId);
            return PickupOrder.pending(commandId);
        }

        @Override
        public ShipmentCancellation cancelShipment(String externalId, String commandId) {
            return ShipmentCancellation.pending(commandId);
        }
    }

    @Test
    void pickupWindowsWithAddressDelegateToTheOldMethod() {
        // given
        ProviderWrittenFor050 provider = new ProviderWrittenFor050();

        // when
        List<PickupWindow> windows = provider.pickupWindows(List.of("1", "2"), PICKUP, LocalDate.of(2026, 10, 9), 4);

        // then
        assertEquals(List.of(WINDOW), windows);
        assertEquals(List.of("pickupWindows [1, 2] 2026-10-09 4"), provider.calls);
    }

    @Test
    void orderPickupWithAddressDelegatesToTheOldMethod() {
        // given
        ProviderWrittenFor050 provider = new ProviderWrittenFor050();

        // when
        PickupOrder order = provider.orderPickup(List.of("1"), PICKUP, WINDOW, "cmd-1");

        // then
        assertEquals(CommandStatus.PENDING, order.status());
        assertEquals(List.of("orderPickup [1] hash-1 cmd-1"), provider.calls);
    }

    @Test
    void newCapabilitiesAreOffByDefault() {
        // given
        ProviderWrittenFor050 provider = new ProviderWrittenFor050();

        // when / then
        assertFalse(provider.supportsShipmentProposals());
        assertFalse(provider.supportsTrackingPolling());
        assertThrows(UnsupportedOperationException.class,
                () -> provider.proposeShipment(new OrderReference("Allegro", "x", "1")));
    }
}
