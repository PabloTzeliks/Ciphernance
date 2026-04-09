package io.ciphernance.identity.domain.exception;

import io.ciphernance.identity.domain.model.enums.UserRole;

import java.util.UUID;

public class InvalidRolePromotionException extends RuntimeException {
    public InvalidRolePromotionException(UUID userId, UserRole role) {
        super("User " + userId + " cannot promote to Role " + role);
    }
}
