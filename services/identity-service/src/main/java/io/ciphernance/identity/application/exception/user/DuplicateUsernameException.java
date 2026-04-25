package io.ciphernance.identity.application.exception.user;

import io.ciphernance.identity.application.exception.base.ResourceAlreadyExistsException;

public class DuplicateUsernameException extends ResourceAlreadyExistsException {
    public DuplicateUsernameException(String username) {
        super("username", username);
    }
}
