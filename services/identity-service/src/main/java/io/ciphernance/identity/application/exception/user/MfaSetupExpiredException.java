package io.ciphernance.identity.application.exception.user;

import io.ciphernance.identity.application.exception.base.AuthenticationException;

import java.util.UUID;

public class MfaSetupExpiredException extends AuthenticationException {
    public MfaSetupExpiredException(UUID userId) {
        super("MFA setup session expired for user: " + userId);
    }
}
