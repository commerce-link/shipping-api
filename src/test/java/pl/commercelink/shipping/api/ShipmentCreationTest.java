package pl.commercelink.shipping.api;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipmentCreationTest {

    private static ShipmentResult result() {
        return new ShipmentResult("21480003",
                List.of(new ShipmentResult.ShipmentParcelResult("0000889416460Q", "dpd", "https://t/1", true, null)), null);
    }

    @Test
    void pendingKnowsTheExternalIdWhenTheProviderGaveIt() {
        // when
        ShipmentCreation creation = ShipmentCreation.pending("cmd-1", "21480003");

        // then
        assertEquals(CommandStatus.PENDING, creation.status());
        assertEquals("21480003", creation.externalId());
        assertNull(creation.result());
    }

    @Test
    void succeededTakesTheExternalIdFromTheResult() {
        // when
        ShipmentCreation creation = ShipmentCreation.succeeded("cmd-1", result());

        // then
        assertEquals(CommandStatus.SUCCEEDED, creation.status());
        assertEquals("21480003", creation.externalId());
        assertEquals("0000889416460Q", creation.result().parcels().get(0).trackingNo());
    }

    @Test
    void succeededWithoutParcelsIsRejected() {
        // when / then
        assertThrows(IllegalArgumentException.class,
                () -> ShipmentCreation.succeeded("cmd-1", new ShipmentResult("1", List.of(), null)));
    }

    @Test
    void failedCarriesTheError() {
        // when
        ShipmentCreation creation = ShipmentCreation.failed("cmd-1", null, "Nieprawidłowy kod pocztowy");

        // then
        assertEquals(CommandStatus.FAILED, creation.status());
        assertEquals("Nieprawidłowy kod pocztowy", creation.error());
    }

    @Test
    void blankPickupNumberMeansNoBookedPickupAndAPickupStillRequired() {
        // when
        ShipmentResult.ShipmentParcelResult parcel = new ShipmentResult.ShipmentParcelResult("X1", "dpd", null, true, " ");

        // then
        assertNull(parcel.pickupNumber());
        assertTrue(parcel.pickupRequired());
    }

    @Test
    void parcelWithAPickupBookedByTheCarrierNeedsNoPickup() {
        // when
        ShipmentResult.ShipmentParcelResult parcel = new ShipmentResult.ShipmentParcelResult("X1", "dpd", null, true,
                "APP/CRIN/13023761");

        // then
        assertEquals("APP/CRIN/13023761", parcel.pickupNumber());
        assertFalse(parcel.pickupRequired());
    }
}
