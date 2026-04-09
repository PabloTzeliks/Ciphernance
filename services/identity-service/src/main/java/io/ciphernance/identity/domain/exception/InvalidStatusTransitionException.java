package io.ciphernance.identity.domain.exception;

import io.ciphernance.identity.domain.vo.UserStatus;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(UserStatus currentStatus, UserStatus newStatus) {
        super("Invalid user status transition from " + currentStatus + " to " + newStatus);
    }
}
