package pl.commercelink.shipping.api;

/**
 * The order a shipment is sent for, as the marketplace knows it. Integrations bound to a marketplace (Wysyłam
 * z Allegro) read the recipient, delivery method and pickup point from that order; others ignore it.
 *
 * @param marketplace     marketplace name as CommerceLink records the order source (e.g. "Allegro")
 * @param externalOrderId the marketplace's order id (Allegro: checkout form id)
 * @param orderNumber     CommerceLink's order number, printed on the label as a reference
 */
public record OrderReference(String marketplace, String externalOrderId, String orderNumber) {
}
