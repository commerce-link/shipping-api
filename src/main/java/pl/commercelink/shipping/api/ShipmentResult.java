package pl.commercelink.shipping.api;

import java.util.List;

public record ShipmentResult(
        String externalId,
        List<ShipmentParcelResult> parcels,
        String managementUrl
) {

    public record ShipmentParcelResult(
            String trackingNo,
            String carrier,
            String trackingUrl
    ) {
    }
}
