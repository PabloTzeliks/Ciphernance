package io.ciphernance.identity.application.command.user;

import io.ciphernance.identity.application.mediator.Command;

public record RegisterUserCommand(
        String username,
        String email,
        String password
) implements Command<RegisterUserResponse> { }
