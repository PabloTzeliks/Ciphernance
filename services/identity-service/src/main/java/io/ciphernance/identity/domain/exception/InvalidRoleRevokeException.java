package io.ciphernance.identity.domain.exception;

import io.ciphernance.identity.domain.model.enums.UserRole;

import java.util.UUID;

public class InvalidRoleRevokeException extends RuntimeException {
    public InvalidRoleRevokeException(UUID id, UserRole role) {
        super("User " + id + " cannot revoke to Role " + role);
    }
}
