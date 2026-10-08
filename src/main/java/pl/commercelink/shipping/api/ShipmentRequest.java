package pl.commercelink.shipping.api;

import java.util.List;

/**
 * orderReference: the marketplace order the shipment is for; null outside marketplace orders. Added in 0.6.0 as the
 * last component; the 0.5.0 constructor is kept for callers compiled against it.
 */
public record ShipmentRequest(
        ShipmentAddress pickup,
        ShipmentAddress sender,
        ShipmentAddress receiver,
        List<Parcel> parcels,
        String carrierId,
        ShipmentOptions options,
        DeliveryPoint deliveryPoint,
        OrderReference orderReference
) {

    public ShipmentRequest(ShipmentAddress pickup, ShipmentAddress sender, ShipmentAddress receiver,
                           List<Parcel> parcels, String carrierId, ShipmentOptions options,
                           DeliveryPoint deliveryPoint) {
        this(pickup, sender, receiver, parcels, carrierId, options, deliveryPoint, null);
    }

    public boolean hasDeliveryPoint() {
        return deliveryPoint != null && deliveryPoint.code() != null && !deliveryPoint.code().isBlank();
    }

    public boolean hasOrderReference() {
        return orderReference != null && orderReference.externalOrderId() != null
                && !orderReference.externalOrderId().isBlank();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ShipmentAddress pickup;
        private ShipmentAddress sender;
        private ShipmentAddress receiver;
        private List<Parcel> parcels = List.of();
        private String carrierId;
        private ShipmentOptions options = ShipmentOptions.NONE;
        private DeliveryPoint deliveryPoint;
        private OrderReference orderReference;

        public Builder pickup(ShipmentAddress pickup) {
            this.pickup = pickup;
            return this;
        }

        public Builder sender(ShipmentAddress sender) {
            this.sender = sender;
            return this;
        }

        public Builder receiver(ShipmentAddress receiver) {
            this.receiver = receiver;
            return this;
        }

        public Builder parcels(List<Parcel> parcels) {
            this.parcels = parcels;
            return this;
        }

        public Builder carrierId(String carrierId) {
            this.carrierId = carrierId;
            return this;
        }

        public Builder options(ShipmentOptions options) {
            this.options = options;
            return this;
        }

        public Builder deliveryPoint(DeliveryPoint deliveryPoint) {
            this.deliveryPoint = deliveryPoint;
            return this;
        }

        public Builder orderReference(OrderReference orderReference) {
            this.orderReference = orderReference;
            return this;
        }

        public ShipmentRequest build() {
            return new ShipmentRequest(pickup, sender, receiver, parcels, carrierId, options, deliveryPoint,
                    orderReference);
        }
    }
}
