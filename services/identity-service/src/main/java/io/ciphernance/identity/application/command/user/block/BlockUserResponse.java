package io.ciphernance.identity.application.command.user.block;

import io.ciphernance.identity.domain.vo.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record BlockUserResponse (

        UUID userId,
        UserStatus previousStatus,
        UserStatus currentStatus,
        Instant blockedAt
) { }
