package io.ciphernance.identity.application.command.user.block;

import io.ciphernance.identity.application.mediator.Command;

import java.util.UUID;

public record BlockUserCommand(

        UUID userId
) implements Command<BlockUserResponse> { }
