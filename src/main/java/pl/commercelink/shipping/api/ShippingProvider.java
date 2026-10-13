package pl.commercelink.shipping.api;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * A shipping integration. Its commands (creating a shipment, ordering a pickup, cancelling) move money or book a
 * courier, so the caller must know whether a failed call left anything behind. The contract of every command method:
 * <ul>
 *   <li>throw {@link ShippingException} only when the command certainly was not accepted: nothing was paid for and no
 *       courier was booked. The caller then reports a refusal and lets the operator try again;</li>
 *   <li>when the provider refused the command with an HTTP 4xx answer, keep that HTTP exception (with its status and
 *       body) as the cause of the {@link ShippingException}: the caller shows the provider's own messages from the body,
 *       and may show them to a customer (e.g. a wrong postcode);</li>
 *   <li>a {@link ShippingException} without an HTTP cause is a check that failed before anything was sent; its message
 *       is shown to the operator only, so it may be technical;</li>
 *   <li>any other failure after the command may have been accepted (an HTTP 5xx, a timeout, a lost connection, an
 *       answer that cannot be read) must not be turned into a {@link ShippingException} without its HTTP cause: return
 *       PENDING (or let the original exception through) so the caller keeps checking the command instead of treating
 *       a possibly paid label or a booked courier as never ordered.</li>
 * </ul>
 */
public interface ShippingProvider {

    List<Carrier> getAvailableCarriers();

    List<ShippingEstimate> estimateShipment(ShipmentRequest request, Set<String> carrierIds);

    /**
     * Starts creating a shipment under a command id chosen by the caller. Usually PENDING: the result is read with
     * {@link #checkShipmentCreation}. Never orders a courier pickup: that is {@link #orderPickup}.
     * <p>
     * Throws {@link ShippingException} only when no label was created or paid for: with the HTTP 4xx cause when the
     * provider refused the shipment, without a cause for a check before sending. After a 5xx, a timeout or an
     * unreadable answer the shipment may exist, so the result is PENDING (with the externalId when known), never a
     * {@link ShippingException} without the HTTP cause.
     */
    ShipmentCreation createShipment(ShipmentRequest request, String commandId);

    /** Reads the creation command; externalId may be null when the start call ended without an answer. */
    ShipmentCreation checkShipmentCreation(String commandId, String externalId);

    /** True when the integration ships orders of one marketplace and can tell what it would ship for one. */
    default boolean supportsShipmentProposals() {
        return false;
    }

    /**
     * What the integration would ship for the marketplace order: the buyer's delivery method with its limits, or an
     * unavailable proposal with the reason (never an exception for an order it cannot ship). Throws
     * {@link ShippingException} only when the provider could not be asked (authorization, outage).
     */
    default ShipmentProposal proposeShipment(OrderReference reference) {
        throw new UnsupportedOperationException("Shipment proposals are not supported by this provider");
    }

    default boolean supportsPickups() {
        return false;
    }

    /** Pickup windows common to all given packages, sorted, available ones only. */
    default List<PickupWindow> pickupWindows(List<String> externalIds, LocalDate readyDate, int daysAhead) {
        throw new UnsupportedOperationException("Courier pickups are not supported by this provider");
    }

    /**
     * Pickup windows for packages collected from the given address. Callers always use this variant; providers that
     * need the address (Wysyłam z Allegro) override it, the default ignores the address for providers built against
     * 0.5.0 (Furgonetka knows the pickup address from the package).
     */
    default List<PickupWindow> pickupWindows(List<String> externalIds, ShipmentAddress pickup, LocalDate readyDate,
                                             int daysAhead) {
        return pickupWindows(externalIds, readyDate, daysAhead);
    }

    /**
     * Orders one courier pickup for all given packages in the window, under a command id chosen by the caller. Usually
     * PENDING: the result is read with {@link #checkPickupOrder}.
     * <p>
     * Throws {@link ShippingException} only when no courier was booked: with the HTTP 4xx cause when the provider
     * refused the pickup, without a cause for a check before sending. After a 5xx, a timeout or an unreadable answer a
     * courier may be on the way, so the result is PENDING, never a {@link ShippingException} without the HTTP cause.
     */
    default PickupOrder orderPickup(List<String> externalIds, PickupWindow window, String commandId) {
        throw new UnsupportedOperationException("Courier pickups are not supported by this provider");
    }

    /** Orders a pickup from the given address; same contract as {@link #orderPickup(List, PickupWindow, String)}. */
    default PickupOrder orderPickup(List<String> externalIds, ShipmentAddress pickup, PickupWindow window,
                                    String commandId) {
        return orderPickup(externalIds, window, commandId);
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

    /**
     * True when statuses are read by asking ({@link #getTrackingEvents}) instead of arriving by webhook; the caller
     * then polls shipments of this integration. Implies {@link #supportsParcelTracking()}.
     */
    default boolean supportsTrackingPolling() {
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
