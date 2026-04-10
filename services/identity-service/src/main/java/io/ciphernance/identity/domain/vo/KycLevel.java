package io.ciphernance.identity.domain.vo;

public enum KycLevel {

    KYC_LEVEL_1,
    KYC_LEVEL_2,
    KYC_LEVEL_3;

    public boolean canPromoteTo(KycLevel next) {
        return next.ordinal() > this.ordinal();
    }
}
