package pl.commercelink.shipping.api;

public record ParcelTrackingSubscription(
        String subscriptionId,
        Status status,
        String externalId,
        String carrier,
        String error
) {

    public enum Status {
        PENDING,
        ACTIVE,
        FAILED
    }

    public static ParcelTrackingSubscription pending(String subscriptionId) {
        return new ParcelTrackingSubscription(subscriptionId, Status.PENDING, null, null, null);
    }

    public static ParcelTrackingSubscription active(String subscriptionId, String externalId, String carrier) {
        return new ParcelTrackingSubscription(subscriptionId, Status.ACTIVE, externalId, carrier, null);
    }

    public static ParcelTrackingSubscription failed(String subscriptionId, String error) {
        return new ParcelTrackingSubscription(subscriptionId, Status.FAILED, null, null, error);
    }
}
