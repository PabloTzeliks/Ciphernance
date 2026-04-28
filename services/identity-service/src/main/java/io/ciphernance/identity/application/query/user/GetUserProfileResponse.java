package io.ciphernance.identity.application.query.user;

import io.ciphernance.identity.domain.model.User;

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
) {

    public static GetUserProfileResponse from(User user) {

        return new GetUserProfileResponse(
                user.getId(),
                user.getUsername().value(),
                user.getEmail().value(),
                user.getRole().name(),
                user.getKycLevel().name(),
                user.isMfaEnabled(),
                user.getStatus().name(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
