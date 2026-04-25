package io.ciphernance.identity.application.port.out;

import io.ciphernance.identity.domain.vo.Email;

public interface TotpGeneratorPort {

    String generateSecret();

    String generateQrCodeUri(String secret, Email email, String issuer);
}
