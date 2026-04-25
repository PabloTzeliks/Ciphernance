package io.ciphernance.identity.application.port.out;

public interface PasswordEncoderPort {

    String encode(String password);

    boolean matches(String rawPassword, String encodedPassword);
}
