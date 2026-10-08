package pl.commercelink.shipping.api;

/** Where an asynchronous provider command stands; the caller picks the command id and reads the status later. */
public enum CommandStatus {
    PENDING,
    SUCCEEDED,
    FAILED
}
