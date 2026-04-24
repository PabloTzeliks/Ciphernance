package io.ciphernance.identity.application.command.user.activate;

import java.time.Instant;
import java.util.UUID;

public record ActivateUserResponse(

        UUID userId,
        Instant executedAt
) { }
