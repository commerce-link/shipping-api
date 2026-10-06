package pl.commercelink.shipping.api;

import java.util.List;

public record ShipmentResult(
        String externalId,
        List<ShipmentParcelResult> parcels,
        String managementUrl
) {

    public ShipmentResult {
        parcels = parcels == null ? List.of() : List.copyOf(parcels);
    }

    /**
     * pickupRequired: a courier pickup still has to be ordered for this parcel (false when it is handed in at a point,
     * or the carrier has no pickups).
     */
    public record ShipmentParcelResult(
            String trackingNo,
            String carrier,
            String trackingUrl,
            boolean pickupRequired
    ) {
    }
}
