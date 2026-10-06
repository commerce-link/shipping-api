package pl.commercelink.shipping.api.testing;

import org.junit.jupiter.api.Test;
import pl.commercelink.shipping.api.CommandStatus;
import pl.commercelink.shipping.api.PickupWindow;
import pl.commercelink.shipping.api.ShipmentCreation;
import pl.commercelink.shipping.api.ShipmentRequest;
import pl.commercelink.shipping.api.ShippingProvider;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Executable contract for {@link ShippingProvider} implementations; every adapter ships a test class extending this
 * kit. Rules: creation is a command under the caller's id and never ends SUCCEEDED without a tracking number; a FAILED
 * result says why; pickup windows are sorted and distinct; a provider without pickups or labels says so and rejects
 * the calls.
 */
public abstract class ShippingProviderContractTest {

    protected abstract ShippingProvider provider();

    protected abstract ShipmentRequest sampleRequest();

    /** Makes the (fake) provider finish the creation command successfully. */
    protected abstract void completeCreation(String commandId);

    /**
     * Makes the (fake) provider finish the creation command with a failure and returns what the provider reports for
     * it. Optional: the default skips the FAILED checks for adapters that cannot simulate a failure.
     */
    protected ShipmentCreation failCreation(String commandId) {
        assumeTrue(false, "This provider test does not simulate a failed creation");
        return null;
    }

    @Test
    void creationAnswersUnderTheCallersCommandId() {
        // when
        ShipmentCreation started = provider().createShipment(sampleRequest(), "contract-cmd-1");

        // then
        assertEquals("contract-cmd-1", started.commandId());
        assertTrue(started.status() == CommandStatus.PENDING || started.status() == CommandStatus.SUCCEEDED);
    }

    @Test
    void completedCreationHasParcelsWithTrackingNumbers() {
        // given
        ShipmentCreation started = provider().createShipment(sampleRequest(), "contract-cmd-2");
        completeCreation("contract-cmd-2");

        // when
        ShipmentCreation done = provider().checkShipmentCreation("contract-cmd-2", started.externalId());

        // then
        assertEquals(CommandStatus.SUCCEEDED, done.status());
        assertNotNull(done.externalId());
        assertFalse(done.result().parcels().isEmpty());
        done.result().parcels().forEach(p -> assertNotNull(p.trackingNo()));
    }

    @Test
    void failedCreationStatesTheReason() {
        // given
        provider().createShipment(sampleRequest(), "contract-cmd-fail");

        // when
        ShipmentCreation failed = failCreation("contract-cmd-fail");

        // then
        assertEquals(CommandStatus.FAILED, failed.status());
        assertNull(failed.result());
        assertNotNull(failed.error());
        assertFalse(failed.error().isBlank());
    }

    @Test
    void pickupWindowsAreSortedAndDistinct() {
        assumeTrue(provider().supportsPickups());
        // given
        ShipmentCreation started = provider().createShipment(sampleRequest(), "contract-cmd-3");
        completeCreation("contract-cmd-3");
        String externalId = provider().checkShipmentCreation("contract-cmd-3", started.externalId()).externalId();

        // when
        List<PickupWindow> windows = provider().pickupWindows(List.of(externalId), LocalDate.now(), 3);

        // then
        List<PickupWindow> sorted = new ArrayList<>(windows);
        sorted.sort(null);
        assertEquals(sorted, windows);
        assertEquals(windows.size(), new HashSet<>(windows).size());
    }

    @Test
    void providerWithoutPickupsRejectsPickupWindows() {
        assumeTrue(!provider().supportsPickups());
        // when / then
        assertThrows(UnsupportedOperationException.class,
                () -> provider().pickupWindows(List.of("1"), LocalDate.now(), 3));
    }

    @Test
    void providerWithoutPickupsRejectsEveryPickupCall() {
        assumeTrue(!provider().supportsPickups());
        // given
        PickupWindow window = new PickupWindow(LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(17, 0), "w");

        // when / then
        assertThrows(UnsupportedOperationException.class,
                () -> provider().pickupWindows(List.of("1"), LocalDate.now(), 3));
        assertThrows(UnsupportedOperationException.class,
                () -> provider().orderPickup(List.of("1"), window, "contract-pickup"));
        assertThrows(UnsupportedOperationException.class,
                () -> provider().checkPickupOrder("contract-pickup"));
    }

    @Test
    void providerWithoutLabelsRejectsLabelCalls() {
        assumeTrue(!provider().supportsLabels());
        // when / then
        assertThrows(UnsupportedOperationException.class, () -> provider().getLabel("1"));
    }
}
