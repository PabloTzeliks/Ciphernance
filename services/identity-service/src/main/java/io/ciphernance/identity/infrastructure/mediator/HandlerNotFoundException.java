package io.ciphernance.identity.infrastructure.mediator;

public class HandlerNotFoundException extends RuntimeException {
    public HandlerNotFoundException(Class<?> type, String kind) {
        super("No " + kind + " handler found for: " + type.getSimpleName());
    }
}

