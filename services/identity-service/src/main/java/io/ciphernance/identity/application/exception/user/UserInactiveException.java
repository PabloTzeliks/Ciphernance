package io.ciphernance.identity.application.exception.user;

import io.ciphernance.identity.application.exception.base.AuthenticationException;
import io.ciphernance.identity.domain.vo.UserStatus;

public class UserInactiveException extends AuthenticationException {
    public UserInactiveException(UserStatus status) {
        super("User is " + status.name().toLowerCase());
    }
}
