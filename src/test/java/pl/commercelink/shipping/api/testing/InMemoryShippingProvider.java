package pl.commercelink.shipping.api.testing;

import pl.commercelink.shipping.api.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

class InMemoryShippingProvider implements ShippingProvider {

    private final Map<String, String> packages = new HashMap<>();
    private final Set<String> completed = new HashSet<>();
    private final Set<String> failed = new HashSet<>();

    void fail(String commandId) {
        failed.add(commandId);
    }

    void complete(String commandId) {
        completed.add(commandId);
    }

    public List<Carrier> getAvailableCarriers() { return List.of(); }

    public List<ShippingEstimate> estimateShipment(ShipmentRequest request, Set<String> carrierIds) { return List.of(); }

    public ShipmentCreation createShipment(ShipmentRequest request, String commandId) {
        String externalId = packages.computeIfAbsent(commandId, id -> "pkg-" + (packages.size() + 1));
        return ShipmentCreation.pending(commandId, externalId);
    }

    public ShipmentCreation checkShipmentCreation(String commandId, String externalId) {
        String id = packages.get(commandId);
        if (failed.contains(commandId)) {
            return ShipmentCreation.failed(commandId, id, "Carrier rejected the shipment");
        }
        if (id == null || !completed.contains(commandId)) {
            return ShipmentCreation.pending(commandId, id);
        }
        return ShipmentCreation.succeeded(commandId, new ShipmentResult(id,
                List.of(new ShipmentResult.ShipmentParcelResult("TRK-" + id, "dpd", null, true)), null));
    }

    public ShipmentCancellation cancelShipment(String externalId, String commandId) {
        return ShipmentCancellation.pending(commandId);
    }

    public boolean supportsPickups() { return true; }

    public List<PickupWindow> pickupWindows(List<String> externalIds, LocalDate readyDate, int daysAhead) {
        return List.of(
                new PickupWindow(readyDate, LocalTime.of(14, 0), LocalTime.of(17, 0), "a"),
                new PickupWindow(readyDate.plusDays(1), LocalTime.of(9, 0), LocalTime.of(17, 0), "b"));
    }
}
