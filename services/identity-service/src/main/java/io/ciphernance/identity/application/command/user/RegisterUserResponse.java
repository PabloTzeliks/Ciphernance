package io.ciphernance.identity.application.command.user;

import io.ciphernance.identity.domain.model.enums.UserRole;
import io.ciphernance.identity.domain.vo.Email;
import io.ciphernance.identity.domain.vo.Username;

import java.util.UUID;

public record RegisterUserResponse(

        UUID id,
        Username username,
        Email email,
        UserRole role,

) { }
