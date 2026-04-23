package io.ciphernance.identity.application.exception.account;

import io.ciphernance.identity.application.exception.base.ResourceNotFoundException;

import java.util.UUID;

public class AccountNotFoundException extends ResourceNotFoundException {
    public AccountNotFoundException(UUID accountId) {
        super("Account", accountId);
    }
}
