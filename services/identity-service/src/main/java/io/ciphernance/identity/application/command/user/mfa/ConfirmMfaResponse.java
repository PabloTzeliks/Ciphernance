package io.ciphernance.identity.application.command.user.mfa;

import java.time.Instant;
import java.util.UUID;

public record ConfirmMfaResponse(

        UUID userId,
        Instant executedAt
) { }
