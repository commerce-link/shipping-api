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
     * the carrier has no pickups, or the pickup is already booked).
     * pickupNumber: the carrier's pickup number when the provider or carrier booked the courier together with the
     * shipment (e.g. a return collected from a customer); null when no pickup is booked yet. A blank number counts as
     * none, and a booked one means no pickup is required, so a provider can pass both as it reads them.
     */
    public record ShipmentParcelResult(
            String trackingNo,
            String carrier,
            String trackingUrl,
            boolean pickupRequired,
            String pickupNumber
    ) {

        public ShipmentParcelResult {
            pickupNumber = pickupNumber == null || pickupNumber.isBlank() ? null : pickupNumber;
            pickupRequired = pickupRequired && pickupNumber == null;
        }
    }
}
