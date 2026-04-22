package io.ciphernance.identity.application.command.user.authenticate;

import io.ciphernance.identity.application.mediator.Command;

public record AuthenticateUserCommand(

        String email,
        String password
) implements Command<AuthenticateUserResponse> { }
