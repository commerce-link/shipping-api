# Shipping API

This library defines a provider-agnostic API for integrating external shipping systems into the CommerceLink platform. It provides a common set of interfaces and data models that shipping provider implementations must adhere to, enabling seamless swapping or coexistence of different shipping backends.

The core `ShippingProvider` interface supports carrier discovery, shipment estimation, shipment creation, cancellation, and webhook processing.

## Provider Discovery

This library extends the [provider-api](https://github.com/commerce-link/provider-api) plugin system. The `ShippingProviderDescriptor` interface extends `ProviderDescriptor<ShippingProvider>` and serves as the SPI entry point for pluggable shipping implementations.

Concrete implementations are discovered at runtime via `ServiceLoader`. See the [provider-api README](https://github.com/commerce-link/provider-api) for registration details.

## Provider contract

`pl.commercelink.shipping.api.testing.ShippingProviderContractTest` is an executable contract for `ShippingProvider`
implementations (JUnit 5, `junit-jupiter-api` is `provided`, so the adapter brings its own). It checks that:

- creation answers under the caller's command id (PENDING or SUCCEEDED);
- a SUCCEEDED creation has an external id and at least one parcel with a tracking number;
- a FAILED creation has no result and a non-blank `error`;
- pickup windows are sorted and distinct;
- a provider without pickups (or labels) throws `UnsupportedOperationException` from every pickup (or label) call.

Extend it in the adapter's tests and implement the hooks:

- `provider()` - the provider under test, backed by a fake or stubbed API;
- `sampleRequest()` - a valid `ShipmentRequest`;
- `completeCreation(commandId)` - makes the fake finish that creation command successfully;
- `failCreation(commandId)` (optional) - makes the fake fail it and returns the provider's answer; without it the
  FAILED check is skipped.
