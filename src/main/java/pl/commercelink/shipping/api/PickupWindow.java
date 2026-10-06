package pl.commercelink.shipping.api;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;

/** A courier pickup time window proposed by the provider; token identifies it at the provider (Furgonetka: hash). */
public record PickupWindow(LocalDate date, LocalTime from, LocalTime to, String token) implements Comparable<PickupWindow> {

    private static final Comparator<PickupWindow> ORDER = Comparator.comparing(PickupWindow::date)
            .thenComparing(PickupWindow::from)
            .thenComparing(PickupWindow::to);

    @Override
    public int compareTo(PickupWindow other) {
        return ORDER.compare(this, other);
    }
}
