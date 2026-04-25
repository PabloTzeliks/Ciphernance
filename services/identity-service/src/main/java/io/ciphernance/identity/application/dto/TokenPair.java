package io.ciphernance.identity.application.dto;

public record TokenPair(

        String accessToken,
        String refreshToken,
        long expiresIn
) { }
