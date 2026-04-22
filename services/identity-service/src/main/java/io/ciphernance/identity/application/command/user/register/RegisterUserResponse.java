package io.ciphernance.identity.application.command.user.register;

import java.time.Instant;
import java.util.UUID;

public record RegisterUserResponse(

        UUID userId,
        UUID accountId,
        String username,
        String email,
        Instant createdAt
) { }
