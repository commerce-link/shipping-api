package pl.commercelink.shipping.api.testing;

import org.junit.jupiter.api.Test;
import pl.commercelink.shipping.api.CommandStatus;
import pl.commercelink.shipping.api.OrderReference;
import pl.commercelink.shipping.api.PickupWindow;
import pl.commercelink.shipping.api.ShipmentAddress;
import pl.commercelink.shipping.api.ShipmentCreation;
import pl.commercelink.shipping.api.ShipmentProposal;
import pl.commercelink.shipping.api.ShipmentRequest;
import pl.commercelink.shipping.api.ShippingProvider;
import pl.commercelink.shipping.api.TrackingEvent;

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
 * the calls. Since 0.6.0: order-bound providers answer a proposal (an unavailable one with a reason, never an
 * exception), pickup calls take the pickup address, and a polling provider tracks parcels. All new checks are skipped
 * unless the adapter test supplies the matching sample.
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

    /** A marketplace order the provider can ship; null skips the proposal checks. */
    protected OrderReference sampleOrderReference() {
        return null;
    }

    /** A marketplace order the provider cannot ship (e.g. own-contract method); null skips that check. */
    protected OrderReference unsupportedOrderReference() {
        return null;
    }

    /** Pickup address passed to the address-taking pickup calls; null skips those checks. */
    protected ShipmentAddress samplePickupAddress() {
        return null;
    }

    /** External id of a created shipment whose tracking can be read; null skips the polling check. */
    protected String sampleTrackedExternalId() {
        return null;
    }

    private static final ShipmentAddress ANY_ADDRESS =
            new ShipmentAddress("Contract", null, "Street 1", "00-001", "City", "PL", "c@example.com", "500600700");

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
        List<PickupWindow> windows = provider().pickupWindows(List.of(externalId), samplePickupAddress(),
                LocalDate.now(), 3);

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

    @Test
    void providerWithoutPickupsRejectsPickupCallsWithAnAddress() {
        assumeTrue(!provider().supportsPickups());
        // given
        PickupWindow window = new PickupWindow(LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(17, 0), "w");

        // when / then
        assertThrows(UnsupportedOperationException.class,
                () -> provider().pickupWindows(List.of("1"), ANY_ADDRESS, LocalDate.now(), 3));
        assertThrows(UnsupportedOperationException.class,
                () -> provider().orderPickup(List.of("1"), ANY_ADDRESS, window, "contract-pickup"));
    }

    @Test
    void proposalForAShippableOrderNamesTheMethodAndAPackage() {
        assumeTrue(provider().supportsShipmentProposals());
        assumeTrue(sampleOrderReference() != null, "This provider test gives no shippable order");
        // when
        ShipmentProposal proposal = provider().proposeShipment(sampleOrderReference());

        // then
        assertTrue(proposal.available());
        assertNotNull(proposal.methodName());
        assertFalse(proposal.packageOptions().isEmpty());
    }

    @Test
    void orderTheProviderCannotShipIsAnUnavailableProposalNotAnException() {
        assumeTrue(provider().supportsShipmentProposals());
        assumeTrue(unsupportedOrderReference() != null, "This provider test gives no unsupported order");
        // when
        ShipmentProposal proposal = provider().proposeShipment(unsupportedOrderReference());

        // then
        assertFalse(proposal.available());
        assertNotNull(proposal.unavailableReason());
        assertFalse(proposal.unavailableReason().isBlank());
    }

    @Test
    void providerWithoutProposalsRejectsProposeShipment() {
        assumeTrue(!provider().supportsShipmentProposals());
        // when / then
        assertThrows(UnsupportedOperationException.class,
                () -> provider().proposeShipment(new OrderReference("Contract", "order-1", "1")));
    }

    @Test
    void pollingProviderTracksParcels() {
        assumeTrue(provider().supportsTrackingPolling());
        // when / then
        assertTrue(provider().supportsParcelTracking());
    }

    @Test
    void trackingEventsOfACreatedShipmentCanBeRead() {
        assumeTrue(provider().supportsTrackingPolling());
        assumeTrue(sampleTrackedExternalId() != null, "This provider test gives no tracked shipment");
        // when
        List<TrackingEvent> events = provider().getTrackingEvents(sampleTrackedExternalId());

        // then
        assertNotNull(events);
    }
}
