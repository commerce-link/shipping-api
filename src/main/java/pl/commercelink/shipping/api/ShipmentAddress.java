package pl.commercelink.shipping.api;

public record ShipmentAddress(
        String name,
        String company,
        String street,
        String postcode,
        String city,
        String countryCode,
        String email,
        String phone
) {
}
