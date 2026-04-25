package io.ciphernance.identity.application.command.user.mfa;

import io.ciphernance.identity.application.exception.user.InvalidMfaCodeException;
import io.ciphernance.identity.application.exception.user.UserNotFoundException;
import io.ciphernance.identity.application.mediator.CommandHandler;
import io.ciphernance.identity.application.port.out.EventPublisherPort;
import io.ciphernance.identity.application.port.out.TotpValidatorPort;
import io.ciphernance.identity.domain.event.DomainEvent;
import io.ciphernance.identity.domain.event.MfaDisabledEvent;
import io.ciphernance.identity.domain.exception.MfaNotEnabledException;
import io.ciphernance.identity.domain.model.User;
import io.ciphernance.identity.domain.port.UserRepositoryPort;
import org.springframework.stereotype.Component;

@Component
public class DisableMfaHandler implements CommandHandler<DisableMfaCommand, Void> {

    private final UserRepositoryPort userRepository;
    private final TotpValidatorPort totpValidator;
    private final EventPublisherPort eventPublisher;

    public DisableMfaHandler(UserRepositoryPort userRepository,
                             TotpValidatorPort totpValidator,
                             EventPublisherPort eventPublisher) {

        this.userRepository = userRepository;
        this.totpValidator = totpValidator;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Void handle(DisableMfaCommand command) {

        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));

        if (!user.isMfaEnabled()) {
            throw new MfaNotEnabledException(command.userId());
        }

        if (!totpValidator.validate(user.getMfaSecret(), command.totpCode())) {
            throw new InvalidMfaCodeException();
        }

        user.disableMfa();
        userRepository.save(user);

        eventPublisher.publish(
                new MfaDisabledEvent(
                        DomainEvent.newEventId(),
                        user.getId(),
                        user.getUpdatedAt()
                )
        );

        return null;
    }
}
