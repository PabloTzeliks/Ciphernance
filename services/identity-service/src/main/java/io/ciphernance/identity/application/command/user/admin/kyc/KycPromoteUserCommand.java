package io.ciphernance.identity.application.command.user.admin.kyc;

import io.ciphernance.identity.application.mediator.Command;
import io.ciphernance.identity.domain.vo.KycLevel;

import java.util.UUID;

public record KycPromoteUserCommand(

        UUID userId,
        KycLevel newKycLevel
) implements Command<Void> { }
