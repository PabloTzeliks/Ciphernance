package io.ciphernance.identity.application.query.user.getuserprofile;

import java.time.Instant;
import java.util.UUID;

public record GetUserProfileResponse(

        UUID id,
        String username,
        String email,
        String role,
        String kycLevel,
        boolean mfaEnabled,
        String status,
        Instant createdAt,
        Instant updatedAt
) { }
