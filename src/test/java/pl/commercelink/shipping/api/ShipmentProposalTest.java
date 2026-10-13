package pl.commercelink.shipping.api;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipmentProposalTest {

    private static final PackageOption PACKAGE = new PackageOption("PACKAGE",
            new BigDecimal("60"), new BigDecimal("50"), new BigDecimal("40"), new BigDecimal("30"));

    @Test
    void availableProposalCarriesMethodAndLimits() {
        // when
        ShipmentProposal proposal = ShipmentProposal.available("Allegro Kurier DPD", "DPD", null, DeliveryType.DOOR,
                List.of(PACKAGE), new BigDecimal("5000"), new BigDecimal("50000"));

        // then
        assertTrue(proposal.available());
        assertNull(proposal.unavailableReason());
        assertEquals("Allegro Kurier DPD", proposal.methodName());
        assertEquals(List.of(PACKAGE), proposal.packageOptions());
        assertEquals(new BigDecimal("5000"), proposal.maxCashOnDelivery());
    }

    @Test
    void packageOptionsAreCopiedAndImmutable() {
        // given
        List<PackageOption> options = new ArrayList<>(List.of(PACKAGE));

        // when
        ShipmentProposal proposal = ShipmentProposal.available("m", "DPD", null, DeliveryType.DOOR, options, null, null);
        options.clear();

        // then
        assertEquals(1, proposal.packageOptions().size());
        assertThrows(UnsupportedOperationException.class, () -> proposal.packageOptions().add(PACKAGE));
    }

    @Test
    void unavailableProposalSaysWhyAndHasNoMethod() {
        // when
        ShipmentProposal proposal = ShipmentProposal.unavailable("Metoda dostawy z Twojej umowy z przewoźnikiem");

        // then
        assertFalse(proposal.available());
        assertEquals("Metoda dostawy z Twojej umowy z przewoźnikiem", proposal.unavailableReason());
        assertNull(proposal.methodName());
        assertTrue(proposal.packageOptions().isEmpty());
    }

    @Test
    void unavailableProposalNeedsAReason() {
        // when / then
        assertThrows(IllegalArgumentException.class, () -> ShipmentProposal.unavailable(" "));
        assertThrows(IllegalArgumentException.class, () -> ShipmentProposal.unavailable(null));
    }

    @Test
    void availableProposalNeedsAMethodName() {
        // when / then
        assertThrows(IllegalArgumentException.class,
                () -> ShipmentProposal.available(" ", "DPD", null, DeliveryType.DOOR, List.of(), null, null));
    }
}
