package io.ciphernance.identity.application.port.out;

public interface TotpValidatorPort {

    boolean validate(String secret, String code);
}
