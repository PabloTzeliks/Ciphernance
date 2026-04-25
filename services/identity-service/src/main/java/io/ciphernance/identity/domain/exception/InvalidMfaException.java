package io.ciphernance.identity.domain.exception;

public class InvalidMfaException extends RuntimeException {
    public InvalidMfaException(String message) {
        super(message);
    }
}
