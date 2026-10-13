package pl.commercelink.shipping.api;

import java.util.List;

/**
 * providerMessages: the provider's own words for a refusal (e.g. Allegro {@code errors[].userMessage}), in the order
 * given; empty when the adapter does not extract them, in which case the caller may read them from the HTTP cause.
 */
public class ShippingException extends RuntimeException {

    private final List<String> providerMessages;

    public ShippingException(String message) {
        this(message, null, List.of());
    }

    public ShippingException(String message, Throwable cause) {
        this(message, cause, List.of());
    }

    public ShippingException(String message, Throwable cause, List<String> providerMessages) {
        super(message, cause);
        this.providerMessages = providerMessages == null ? List.of() : List.copyOf(providerMessages);
    }

    public List<String> providerMessages() {
        return providerMessages;
    }
}
