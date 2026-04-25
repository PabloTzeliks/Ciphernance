package io.ciphernance.identity.application.command.user.admin.revoke;

import io.ciphernance.identity.application.mediator.Command;

import java.util.UUID;

public record RevokeAdminCommand(

        UUID userId
) implements Command<Void> { }
