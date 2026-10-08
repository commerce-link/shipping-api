package pl.commercelink.shipping.api;

/** A shipping label file as the provider returns it (PDF or ZPL). */
public record Label(byte[] content, String contentType, String fileName) {
}
