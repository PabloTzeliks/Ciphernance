package io.ciphernance.identity.application.exception.account;

import io.ciphernance.identity.application.exception.base.ResourceNotFoundException;

import java.util.UUID;

public class AccountNotFoundException extends ResourceNotFoundException {

    public AccountNotFoundException(UUID accountId) {
        super("Account not found: ", accountId);
    }

    private AccountNotFoundException(String message) {
        super(message);
    }

    public static AccountNotFoundException forOwner(UUID ownerId) {
        return new AccountNotFoundException(
                "Account not found for user: " + ownerId
        );
    }
}
