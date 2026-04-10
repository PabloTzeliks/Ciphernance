package io.ciphernance.identity.domain.exception;

import io.ciphernance.identity.domain.vo.UserStatus;

public class InvalidUserStatusTransitionException extends RuntimeException {
    public InvalidUserStatusTransitionException(UserStatus currentStatus, UserStatus newStatus) {
        super("Invalid User Status transition from " + currentStatus + " to " + newStatus);
    }
}
