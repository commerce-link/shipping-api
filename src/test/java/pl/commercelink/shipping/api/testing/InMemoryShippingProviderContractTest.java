package pl.commercelink.shipping.api.testing;

import pl.commercelink.shipping.api.ShipmentCreation;
import pl.commercelink.shipping.api.ShipmentRequest;
import pl.commercelink.shipping.api.ShippingProvider;

class InMemoryShippingProviderContractTest extends ShippingProviderContractTest {

    private final InMemoryShippingProvider provider = new InMemoryShippingProvider();

    protected ShippingProvider provider() { return provider; }

    protected ShipmentRequest sampleRequest() { return ShipmentRequest.builder().build(); }

    protected void completeCreation(String commandId) { provider.complete(commandId); }

    @Override
    protected ShipmentCreation failCreation(String commandId) {
        provider.fail(commandId);
        return provider.checkShipmentCreation(commandId, null);
    }
}
