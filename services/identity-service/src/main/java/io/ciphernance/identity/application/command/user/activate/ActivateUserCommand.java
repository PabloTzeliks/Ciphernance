package io.ciphernance.identity.application.command.user.activate;

import io.ciphernance.identity.application.mediator.Command;

import java.util.UUID;

public record ActivateUserCommand(

        UUID userId
) implements Command<Void> { }
