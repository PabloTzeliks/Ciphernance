package io.ciphernance.identity.domain.exception;

public class InvalidMfaExeception extends RuntimeException {
    public InvalidMfaExeception(String message) {
        super(message);
    }
}
