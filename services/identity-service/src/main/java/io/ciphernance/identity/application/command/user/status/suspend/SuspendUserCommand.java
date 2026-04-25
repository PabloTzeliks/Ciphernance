package io.ciphernance.identity.application.command.user.status.suspend;

import io.ciphernance.identity.application.mediator.Command;

import java.util.UUID;

public record SuspendUserCommand(

        UUID userId
) implements Command<Void> { }
