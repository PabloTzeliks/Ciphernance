package io.ciphernance.identity.domain.exception;

import io.ciphernance.identity.domain.vo.KycLevel;

public class InvalidKycPromotionException extends RuntimeException {
    public InvalidKycPromotionException(KycLevel currentLevel, KycLevel newLevel) {
        super("Invalid KYC level transition from " + currentLevel + " to " + newLevel);
    }
}
