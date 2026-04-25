package io.ciphernance.identity.application.command.user.mfa.confirm;

import io.ciphernance.identity.application.exception.user.InvalidMfaCodeException;
import io.ciphernance.identity.application.exception.user.MfaSetupExpiredException;
import io.ciphernance.identity.application.exception.user.UserNotFoundException;
import io.ciphernance.identity.application.mediator.CommandHandler;
import io.ciphernance.identity.application.port.out.EventPublisherPort;
import io.ciphernance.identity.application.port.out.MfaSetupCachePort;
import io.ciphernance.identity.application.port.out.TotpValidatorPort;
import io.ciphernance.identity.domain.event.DomainEvent;
import io.ciphernance.identity.domain.event.MfaEnabledEvent;
import io.ciphernance.identity.domain.model.User;
import io.ciphernance.identity.domain.port.UserRepositoryPort;
import org.springframework.stereotype.Component;

@Component
public class ConfirmMfaHandler implements CommandHandler<ConfirmMfaCommand, ConfirmMfaResponse> {

    private final UserRepositoryPort userRepository;
    private final TotpValidatorPort totpValidator;
    private final MfaSetupCachePort mfaSetupCache;
    private final EventPublisherPort eventPublisher;

    public ConfirmMfaHandler(UserRepositoryPort userRepository,
                             TotpValidatorPort totpValidator,
                             MfaSetupCachePort mfaSetupCache,
                             EventPublisherPort eventPublisher) {

        this.userRepository = userRepository;
        this.totpValidator = totpValidator;
        this.mfaSetupCache = mfaSetupCache;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public ConfirmMfaResponse handle(ConfirmMfaCommand command) {

        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));

        String secret = mfaSetupCache.retrieve(command.userId())
                .orElseThrow(() -> new MfaSetupExpiredException(command.userId()));

        if (!totpValidator.validate(secret, command.totpCode())) {
            throw new InvalidMfaCodeException();
        }

        user.enableMfa(secret);
        userRepository.save(user);

        mfaSetupCache.delete(command.userId());

        eventPublisher.publish(
                new MfaEnabledEvent(
                        DomainEvent.newEventId(),
                        user.getId(),
                        user.getUpdatedAt()
                )
        );

        return new ConfirmMfaResponse(
                user.getId(),
                user.getUpdatedAt()
        );
    }
}
