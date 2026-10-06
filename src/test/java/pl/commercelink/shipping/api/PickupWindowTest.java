package pl.commercelink.shipping.api;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PickupWindowTest {

    @Test
    void windowsSortByDateThenStartThenEnd() {
        // given
        PickupWindow lateDay = new PickupWindow(LocalDate.of(2026, 10, 7), LocalTime.of(9, 0), LocalTime.of(17, 0), "b");
        PickupWindow early = new PickupWindow(LocalDate.of(2026, 10, 6), LocalTime.of(14, 0), LocalTime.of(17, 0), "a");
        PickupWindow sameDayShorter = new PickupWindow(LocalDate.of(2026, 10, 7), LocalTime.of(9, 0), LocalTime.of(12, 0), "c");
        List<PickupWindow> windows = new ArrayList<>(List.of(lateDay, early, sameDayShorter));

        // when
        windows.sort(null);

        // then
        assertEquals(List.of(early, sameDayShorter, lateDay), windows);
    }
}
