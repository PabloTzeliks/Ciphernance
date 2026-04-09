package io.ciphernance.identity.domain.exception;

import java.util.UUID;

public class MfaNotEnabledException extends RuntimeException {
    public MfaNotEnabledException(UUID userId) {
        super("User " + userId + " is not MFA enabled");
    }
}
