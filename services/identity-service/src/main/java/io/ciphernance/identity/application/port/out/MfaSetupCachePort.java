package io.ciphernance.identity.application.port.out;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

public interface MfaSetupCachePort {

    void store(UUID userId, String secret, Duration ttl);

    Optional<String> retrieve(UUID userId);

    void delete(UUID userId);
}
