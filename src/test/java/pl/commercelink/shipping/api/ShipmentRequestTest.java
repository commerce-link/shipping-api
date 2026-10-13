package pl.commercelink.shipping.api;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipmentRequestTest {

    private static final ShipmentAddress ADDRESS =
            new ShipmentAddress("Jan", null, "Prosta 1", "00-001", "Warszawa", "PL", "jan@example.com", "500600700");

    @Test
    void sevenArgumentConstructorLeavesNoOrderReference() {
        // when: the constructor callers compiled against 0.5.0 use
        ShipmentRequest request = new ShipmentRequest(ADDRESS, ADDRESS, ADDRESS, List.of(), "dpd",
                ShipmentOptions.NONE, null);

        // then
        assertNull(request.orderReference());
        assertFalse(request.hasOrderReference());
    }

    @Test
    void builderCarriesTheOrderReference() {
        // given
        OrderReference reference = new OrderReference("Allegro", "29f1a1d0-ab8f-11f1-8456-8d3ada2e8e1c", "1024");

        // when
        ShipmentRequest request = ShipmentRequest.builder().receiver(ADDRESS).orderReference(reference).build();

        // then
        assertEquals(reference, request.orderReference());
        assertTrue(request.hasOrderReference());
    }

    @Test
    void referenceWithoutExternalOrderIdDoesNotCount() {
        // when
        ShipmentRequest request = ShipmentRequest.builder()
                .orderReference(new OrderReference("Allegro", " ", "1024")).build();

        // then
        assertFalse(request.hasOrderReference());
    }
}
