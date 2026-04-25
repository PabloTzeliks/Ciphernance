package io.ciphernance.identity.domain.port;

import io.ciphernance.identity.domain.model.User;
import io.ciphernance.identity.domain.vo.Email;
import io.ciphernance.identity.domain.vo.Username;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByUsername(Username username);

    Optional<User> findByEmail(Email email);

    boolean existsByUsername(Username username);

    boolean existsByEmail(Email email);
}
