package io.ciphernance.identity.domain.port;

import io.ciphernance.identity.domain.model.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepositoryPort {

    Account save(Account account);
    Optional<Account> findById(UUID id);
    Optional<Account> findByOwnerId(UUID userId);
    boolean existsByOwnerId(UUID userId);
}
