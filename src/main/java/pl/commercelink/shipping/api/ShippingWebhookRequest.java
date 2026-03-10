package pl.commercelink.shipping.api;

import java.util.Map;

public record ShippingWebhookRequest(String payload, Map<String, String> headers) {

    public String getHeader(String name) {
        String value = headers.get(name);
        if (value != null) {
            return value;
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(name)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
