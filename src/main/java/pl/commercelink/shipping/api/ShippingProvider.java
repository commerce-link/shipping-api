package pl.commercelink.shipping.api;

import java.util.List;
import java.util.Set;

public interface ShippingProvider {

    List<Carrier> getAvailableCarriers();

    List<ShippingEstimate> estimateShipment(ShipmentRequest request, Set<String> carrierIds);

    ShipmentResult createShipment(ShipmentRequest request);

    void cancelShipment(String externalId);

    ShippingWebhookResult processWebhook(ShippingWebhookRequest request);
}
