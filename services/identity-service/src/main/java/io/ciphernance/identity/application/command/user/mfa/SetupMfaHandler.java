package io.ciphernance.identity.application.command.user.mfa;

import io.ciphernance.identity.application.exception.user.UserNotFoundException;
import io.ciphernance.identity.application.mediator.CommandHandler;
import io.ciphernance.identity.application.port.out.EventPublisherPort;
import io.ciphernance.identity.application.port.out.MfaSetupCachePort;
import io.ciphernance.identity.application.port.out.TotpGeneratorPort;
import io.ciphernance.identity.application.port.out.TotpValidatorPort;
import io.ciphernance.identity.domain.model.User;
import io.ciphernance.identity.domain.port.UserRepositoryPort;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class SetupMfaHandler implements CommandHandler<SetupMfaCommand, SetupMfaResponse> {

    private final UserRepositoryPort userRepository;
    private final TotpGeneratorPort totpGenerator;
    private final MfaSetupCachePort mfaCachePort;
    private final EventPublisherPort eventPublisher;

    public SetupMfaHandler(UserRepositoryPort userRepository,
                           TotpGeneratorPort totpGenerator,
                           MfaSetupCachePort mfaCachePort,
                           EventPublisherPort eventPublisher) {

        this.userRepository = userRepository;
        this.totpGenerator = totpGenerator;
        this.mfaCachePort = mfaCachePort;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public SetupMfaResponse handle(SetupMfaCommand command) {

        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));

        String mfaSecret = totpGenerator.generateSecret();

        String qrCodeUri = totpGenerator.generateQrCodeUri(
                mfaSecret,
                user.getEmail(),
                "Ciphernance"
        );

        mfaCachePort.store(user.getId(), mfaSecret, Duration.ofMinutes(5));

        return new SetupMfaResponse(
                user.getId(),
                qrCodeUri,
                mfaSecret
        );
    }
}
