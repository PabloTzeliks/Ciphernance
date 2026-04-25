package io.ciphernance.identity.application.command.user.admin.promote;

import io.ciphernance.identity.application.mediator.Command;

import java.util.UUID;

public record PromoteToAdminCommand(

        UUID userId
) implements Command<Void> { }
