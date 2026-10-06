package pl.commercelink.shipping.api;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PickupOrderTest {

    private static final PickupWindow WINDOW =
            new PickupWindow(LocalDate.of(2026, 10, 6), LocalTime.of(14, 0), LocalTime.of(17, 0), "h");

    @Test
    void externalIdsAreCopiedAndImmutable() {
        // given
        List<String> ids = new ArrayList<>(List.of("1", "2"));

        // when
        PickupOrder order = PickupOrder.pending("cmd-1", ids, WINDOW);
        ids.add("3");

        // then
        assertEquals(List.of("1", "2"), order.externalIds());
        assertThrows(UnsupportedOperationException.class, () -> order.externalIds().add("x"));
    }

    @Test
    void succeededCarriesPickupIdAndWindow() {
        // when
        PickupOrder order = PickupOrder.succeeded("cmd-1", "20261006800071", WINDOW, List.of("1", "2"));

        // then
        assertEquals(CommandStatus.SUCCEEDED, order.status());
        assertEquals("20261006800071", order.pickupId());
        assertEquals(WINDOW, order.window());
    }
}
