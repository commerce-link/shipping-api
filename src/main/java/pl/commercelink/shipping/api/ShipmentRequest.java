package pl.commercelink.shipping.api;

import java.util.List;

public record ShipmentRequest(
        ShipmentAddress pickup,
        ShipmentAddress sender,
        ShipmentAddress receiver,
        List<Parcel> parcels,
        String carrierId,
        ShipmentOptions options
) {

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

        public ShipmentRequest build() {
            return new ShipmentRequest(pickup, sender, receiver, parcels, carrierId, options);
        }
    }
}
