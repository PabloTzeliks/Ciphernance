package io.ciphernance.identity.application.exception.user;

import io.ciphernance.identity.application.exception.base.AuthenticationException;

public class InvalidCredentialsException extends AuthenticationException {
    public InvalidCredentialsException() {
        super("Invalid credentials");
    }
}
