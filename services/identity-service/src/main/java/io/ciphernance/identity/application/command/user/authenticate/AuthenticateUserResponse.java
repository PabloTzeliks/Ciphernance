package io.ciphernance.identity.application.command.user.authenticate;

public record AuthenticateUserResponse(

        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) { }
