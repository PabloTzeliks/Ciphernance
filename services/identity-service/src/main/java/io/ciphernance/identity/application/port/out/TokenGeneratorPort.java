package io.ciphernance.identity.application.port.out;

import io.ciphernance.identity.application.dto.TokenClaims;
import io.ciphernance.identity.application.dto.TokenPair;

import java.util.UUID;

public interface TokenGeneratorPort {

    TokenPair generate(TokenClaims claims);

    String generateMfaPending(UUID userId);
}
