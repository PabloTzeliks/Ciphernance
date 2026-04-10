package io.ciphernance.identity.domain.ports;

import io.ciphernance.identity.domain.model.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepositoryPort {

    Account save(Account account);
    Optional<Account> findById(UUID id);
    Optional<Account> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
}
