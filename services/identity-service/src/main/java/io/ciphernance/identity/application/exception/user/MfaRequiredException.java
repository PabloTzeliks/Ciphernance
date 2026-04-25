package io.ciphernance.identity.application.exception.user;

import io.ciphernance.identity.application.exception.base.AuthenticationException;

public class MfaRequiredException extends AuthenticationException {
    private final String mfaPendingToken;

    public MfaRequiredException(String mfaPendingToken) {
        super("MFA verification required");
        this.mfaPendingToken = mfaPendingToken;
    }

    public String getMfaPendingToken() {
        return mfaPendingToken;
    }
}
