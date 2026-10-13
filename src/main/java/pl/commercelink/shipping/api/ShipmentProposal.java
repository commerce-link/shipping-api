package pl.commercelink.shipping.api;

import java.math.BigDecimal;
import java.util.List;

/**
 * What an order-bound integration would ship for an order: the delivery method the buyer chose and its limits, or why
 * the integration cannot ship this order (e.g. the method runs on the seller's own carrier contract). An unavailable
 * proposal is an answer, not an error: the caller shows the reason next to the integration.
 */
public record ShipmentProposal(
        boolean available,
        String unavailableReason,
        String methodName,
        String carrierId,
        DeliveryPoint deliveryPoint,
        DeliveryType deliveryType,
        List<PackageOption> packageOptions,
        BigDecimal maxCashOnDelivery,
        BigDecimal maxInsurance
) {

    public ShipmentProposal {
        packageOptions = packageOptions == null ? List.of() : List.copyOf(packageOptions);
    }

    public static ShipmentProposal available(String methodName, String carrierId, DeliveryPoint deliveryPoint,
                                             DeliveryType deliveryType, List<PackageOption> packageOptions,
                                             BigDecimal maxCashOnDelivery, BigDecimal maxInsurance) {
        if (methodName == null || methodName.isBlank()) {
            throw new IllegalArgumentException("An available proposal names the delivery method");
        }
        return new ShipmentProposal(true, null, methodName, carrierId, deliveryPoint, deliveryType, packageOptions,
                maxCashOnDelivery, maxInsurance);
    }

    public static ShipmentProposal unavailable(String reason) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("An unavailable proposal says why");
        }
        return new ShipmentProposal(false, reason, null, null, null, null, List.of(), null, null);
    }
}
