package io.ciphernance.identity.application.command.user.kyc;

import java.time.Instant;
import java.util.UUID;

public record KycPromoteUserResponse(

        UUID userId,
        Instant executedAt
) { }
