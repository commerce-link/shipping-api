package pl.commercelink.shipping.api;

import java.util.List;

/**
 * Outcome of a courier pickup command for one or more packages of the same carrier. externalIds: the packages the
 * provider booked, set only when SUCCEEDED; empty means all packages of the command.
 */
public record PickupOrder(
        String commandId,
        CommandStatus status,
        String pickupId,
        List<String> externalIds,
        String error
) {

    public PickupOrder {
        externalIds = externalIds == null ? List.of() : List.copyOf(externalIds);
    }

    public static PickupOrder pending(String commandId) {
        return new PickupOrder(commandId, CommandStatus.PENDING, null, List.of(), null);
    }

    public static PickupOrder succeeded(String commandId, String pickupId, List<String> externalIds) {
        return new PickupOrder(commandId, CommandStatus.SUCCEEDED, pickupId, externalIds, null);
    }

    public static PickupOrder failed(String commandId, String error) {
        return new PickupOrder(commandId, CommandStatus.FAILED, null, List.of(), error);
    }
}
