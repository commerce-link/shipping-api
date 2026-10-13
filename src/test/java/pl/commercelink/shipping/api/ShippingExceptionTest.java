package pl.commercelink.shipping.api;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShippingExceptionTest {

    @Test
    void existingConstructorsHaveNoProviderMessages() {
        // when / then
        assertTrue(new ShippingException("refused").providerMessages().isEmpty());
        assertTrue(new ShippingException("refused", new RuntimeException()).providerMessages().isEmpty());
    }

    @Test
    void providerMessagesAreCopiedAndImmutable() {
        // given
        List<String> messages = new ArrayList<>(List.of("Nieprawidłowy kod pocztowy odbiorcy"));
        RuntimeException cause = new RuntimeException("HTTP 422");

        // when
        ShippingException e = new ShippingException("Allegro refused the shipment", cause, messages);
        messages.add("other");

        // then
        assertEquals(List.of("Nieprawidłowy kod pocztowy odbiorcy"), e.providerMessages());
        assertSame(cause, e.getCause());
        assertThrows(UnsupportedOperationException.class, () -> e.providerMessages().add("x"));
    }

    @Test
    void nullProviderMessagesMeanNone() {
        // when / then
        assertTrue(new ShippingException("refused", null, null).providerMessages().isEmpty());
    }
}
