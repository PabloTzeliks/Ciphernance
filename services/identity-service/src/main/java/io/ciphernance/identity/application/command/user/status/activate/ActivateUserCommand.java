package io.ciphernance.identity.application.command.user.status.activate;

import io.ciphernance.identity.application.mediator.Command;

import java.util.UUID;

public record ActivateUserCommand(

        UUID userId
) implements Command<Void> { }
