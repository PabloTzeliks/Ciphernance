package io.ciphernance.identity.application.command.user.mfa.setup;

import io.ciphernance.identity.application.mediator.Command;

import java.util.UUID;

public record SetupMfaCommand(

        UUID userId
) implements Command<SetupMfaResponse> { }
