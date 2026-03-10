package pl.commercelink.shipping.api;

public record ShipmentOptions(
        boolean saturdayDelivery,
        boolean printLabel,
        CashOnDelivery cashOnDelivery
) {

    public static final ShipmentOptions NONE = new ShipmentOptions(false, false, null);

    public boolean hasCashOnDelivery() {
        return cashOnDelivery != null;
    }

    public record CashOnDelivery(
            double amount,
            String iban,
            String accountHolder,
            String swiftCode
    ) {
    }
}
