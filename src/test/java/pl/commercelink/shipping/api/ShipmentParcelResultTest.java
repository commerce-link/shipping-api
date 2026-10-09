package pl.commercelink.shipping.api;

import org.junit.jupiter.api.Test;
import pl.commercelink.shipping.api.ShipmentResult.ShipmentParcelResult;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipmentParcelResultTest {

    @Test
    void fiveArgumentConstructorMeansCancellable() {
        // when: the constructor shipping-furgonetka 0.5.0 calls
        ShipmentParcelResult parcel = new ShipmentParcelResult("0000889416460Q", "dpd", null, true, null);

        // then
        assertTrue(parcel.cancellable());
    }

    @Test
    void cancellableCanBeDenied() {
        // when
        ShipmentParcelResult parcel = new ShipmentParcelResult("A000BC1234", "ALLEGRO", null, true, null, false);

        // then
        assertFalse(parcel.cancellable());
    }

    @Test
    void pickupNumberNormalizationStaysInBothConstructors() {
        // when
        ShipmentParcelResult blank = new ShipmentParcelResult("1", "dpd", null, true, " ");
        ShipmentParcelResult booked = new ShipmentParcelResult("2", "dpd", null, true, "20261006800071", true);

        // then
        assertNull(blank.pickupNumber());
        assertTrue(blank.pickupRequired());
        assertFalse(booked.pickupRequired());
    }
}
