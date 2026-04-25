package io.ciphernance.identity.application.command.user.authenticate.login;

import io.ciphernance.identity.application.mediator.Command;

public record LoginUserCommand(

        String email,
        String password,
        String totpCode
) implements Command<LoginUserResponse> { }
