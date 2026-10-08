package pl.commercelink.shipping.api;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PickupOrderTest {

    @Test
    void externalIdsAreCopiedAndImmutable() {
        // given
        List<String> ids = new ArrayList<>(List.of("1", "2"));

        // when
        PickupOrder order = PickupOrder.succeeded("cmd-1", "20261006800071", ids);
        ids.add("3");

        // then
        assertEquals(List.of("1", "2"), order.externalIds());
        assertThrows(UnsupportedOperationException.class, () -> order.externalIds().add("x"));
    }

    @Test
    void succeededCarriesPickupId() {
        // when
        PickupOrder order = PickupOrder.succeeded("cmd-1", "20261006800071", List.of("1", "2"));

        // then
        assertEquals(CommandStatus.SUCCEEDED, order.status());
        assertEquals("20261006800071", order.pickupId());
    }
}
