package io.ciphernance.identity.application.exception.user;

import io.ciphernance.identity.application.exception.base.AuthenticationException;

public class InvalidMfaCodeException extends AuthenticationException {
    public InvalidMfaCodeException() {
        super("Invalid or expired MFA code");
    }
}