package io.ciphernance.identity.application.dto;

import io.ciphernance.identity.domain.model.enums.AuthLevel;

import java.util.UUID;

public record TokenClaims(

        UUID userId,
        String role,
        String kycLevel,
        UUID accountId,
        String accountStatus,
        boolean mfaEnabled,
        AuthLevel authLevel
) {}
