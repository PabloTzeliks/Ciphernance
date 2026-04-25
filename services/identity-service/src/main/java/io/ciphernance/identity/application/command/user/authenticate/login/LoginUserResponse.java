package io.ciphernance.identity.application.command.user.authenticate.login;

public record LoginUserResponse(

        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) { }
