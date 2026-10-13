package pl.commercelink.shipping.api;

import java.math.BigDecimal;

/**
 * A package type the delivery method accepts with its limits; dimensions in centimetres, weight in kilograms, null
 * means no limit given by the provider.
 */
public record PackageOption(
        String type,
        BigDecimal maxLength,
        BigDecimal maxWidth,
        BigDecimal maxHeight,
        BigDecimal maxWeight
) {
}
