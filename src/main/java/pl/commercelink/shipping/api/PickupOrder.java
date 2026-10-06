package pl.commercelink.shipping.api;

import java.util.List;

/** Outcome of a courier pickup command for one or more packages of the same carrier. */
public record PickupOrder(
        String commandId,
        CommandStatus status,
        String pickupId,
        PickupWindow window,
        List<String> externalIds,
        String error
) {

    public PickupOrder {
        externalIds = externalIds == null ? List.of() : List.copyOf(externalIds);
    }

    public static PickupOrder pending(String commandId, List<String> externalIds, PickupWindow window) {
        return new PickupOrder(commandId, CommandStatus.PENDING, null, window, externalIds, null);
    }

    public static PickupOrder succeeded(String commandId, String pickupId, PickupWindow window, List<String> externalIds) {
        return new PickupOrder(commandId, CommandStatus.SUCCEEDED, pickupId, window, externalIds, null);
    }

    public static PickupOrder failed(String commandId, List<String> externalIds, String error) {
        return new PickupOrder(commandId, CommandStatus.FAILED, null, null, externalIds, error);
    }
}
