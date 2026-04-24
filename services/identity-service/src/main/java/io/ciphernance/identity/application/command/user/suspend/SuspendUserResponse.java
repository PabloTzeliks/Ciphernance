package io.ciphernance.identity.application.command.user.suspend;

import java.time.Instant;
import java.util.UUID;

public record SuspendUserResponse(

        UUID userId,
        Instant executedAt
) { }
