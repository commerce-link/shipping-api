package pl.commercelink.shipping.api;

import java.time.LocalDateTime;

public record ShippingWebhookResult(String trackingNo, ShipmentState state, LocalDateTime datetime) {

    public enum ShipmentState {
        COLLECTED,
        DELIVERED,
        OTHER
    }
}
