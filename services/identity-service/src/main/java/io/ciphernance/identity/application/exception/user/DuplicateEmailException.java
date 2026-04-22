package io.ciphernance.identity.application.exception.user;

import io.ciphernance.identity.application.exception.base.ResourceAlreadyExistsException;

public class DuplicateEmailException extends ResourceAlreadyExistsException {
    public DuplicateEmailException(String email) {
        super("email", email);
    }
}
