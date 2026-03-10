package pl.commercelink.shipping.api;

import java.math.BigDecimal;
import java.util.List;

public record ShippingEstimate(
        String carrierId,
        String carrierName,
        boolean available,
        BigDecimal priceGross,
        BigDecimal priceNet,
        List<String> errors
) {

    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }
}
