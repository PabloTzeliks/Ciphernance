package io.ciphernance.identity.application.exception.account;

import io.ciphernance.identity.application.exception.base.AuthenticationException;
import io.ciphernance.identity.domain.vo.UserStatus;

public class AccountInactiveException extends AuthenticationException {
    public AccountInactiveException(UserStatus status) {
        super("Account is " + status.name().toLowerCase());
    }
}
