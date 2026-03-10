# Shipping API

This library defines a provider-agnostic API for integrating external shipping systems into the CommerceLink platform. It provides a common set of interfaces and data models that shipping provider implementations must adhere to, enabling seamless swapping or coexistence of different shipping backends.

The core `ShippingProvider` interface supports carrier discovery, shipment estimation, shipment creation, cancellation, and webhook processing.

## Provider Discovery

This library extends the [provider-api](https://github.com/commerce-link/provider-api) plugin system. The `ShippingProviderDescriptor` interface extends `ProviderDescriptor<ShippingProvider>` and serves as the SPI entry point for pluggable shipping implementations.

Concrete implementations are discovered at runtime via `ServiceLoader`. See the [provider-api README](https://github.com/commerce-link/provider-api) for registration details.
