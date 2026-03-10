package pl.commercelink.shipping.api;

import java.time.OffsetDateTime;

public record TrackingEvent(
        String state,
        String status,
        OffsetDateTime datetime
) {
}
