package io.ciphernance.identity.domain.exception;

import java.util.UUID;

public class MfaAlreadyEnabledException extends RuntimeException {
    public MfaAlreadyEnabledException(UUID userId) {
        super("User " + userId + " is already MFA enabled");
    }
}
