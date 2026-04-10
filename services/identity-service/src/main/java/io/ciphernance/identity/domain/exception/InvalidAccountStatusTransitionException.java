package io.ciphernance.identity.domain.exception;

import io.ciphernance.identity.domain.vo.AccountStatus;

public class InvalidAccountStatusTransitionException extends RuntimeException {
    public InvalidAccountStatusTransitionException(AccountStatus currentStatus, AccountStatus newStatus) {
        super("Invalid Account Status transition from " + currentStatus + " to " + newStatus);
    }
}
