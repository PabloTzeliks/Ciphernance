package io.ciphernance.identity.application.command.user.mfa;

import io.ciphernance.identity.application.mediator.Command;

import java.util.UUID;

public record DisableMfaCommand(

        UUID userId,
        String totpCode
) implements Command<Void> { }
