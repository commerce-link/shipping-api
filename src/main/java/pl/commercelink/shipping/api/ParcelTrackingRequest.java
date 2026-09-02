package pl.commercelink.shipping.api;

public record ParcelTrackingRequest(String trackingNo, String carrier, String label) {
}
