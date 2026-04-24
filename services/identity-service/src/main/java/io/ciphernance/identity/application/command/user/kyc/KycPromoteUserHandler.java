package io.ciphernance.identity.application.command.user.kyc;

import io.ciphernance.identity.application.exception.user.UserNotFoundException;
import io.ciphernance.identity.application.mediator.CommandHandler;
import io.ciphernance.identity.application.port.out.EventPublisherPort;
import io.ciphernance.identity.domain.event.DomainEvent;
import io.ciphernance.identity.domain.event.KycLevelUpdatedEvent;
import io.ciphernance.identity.domain.model.User;
import io.ciphernance.identity.domain.port.UserRepositoryPort;
import io.ciphernance.identity.domain.vo.KycLevel;
import org.springframework.stereotype.Component;

@Component
public class KycPromoteUserHandler implements CommandHandler<KycPromoteUserCommand, KycPromoteUserResponse> {

    private final UserRepositoryPort userRepository;
    private final EventPublisherPort eventPublisher;

    public KycPromoteUserHandler(UserRepositoryPort userRepository, EventPublisherPort eventPublisher) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public KycPromoteUserResponse handle(KycPromoteUserCommand command) {

        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));

        KycLevel previousLevel = user.getKycLevel();

        user.promoteKyc(command.newKycLevel());

        userRepository.save(user);

        eventPublisher.publish(
                new KycLevelUpdatedEvent(
                        DomainEvent.newEventId(),
                        user.getId(),
                        previousLevel,
                        user.getKycLevel(),
                        user.getUpdatedAt()
                )
        );

        return new KycPromoteUserResponse(
                user.getId(),
                user.getUpdatedAt()
        );
    }
}
