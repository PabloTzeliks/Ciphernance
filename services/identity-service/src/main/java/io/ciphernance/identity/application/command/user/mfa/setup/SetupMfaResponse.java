package io.ciphernance.identity.application.command.user.mfa.setup;

import java.util.UUID;

public record SetupMfaResponse(

        UUID userId,
        String qrCodeUri,
        String secret
) { }
