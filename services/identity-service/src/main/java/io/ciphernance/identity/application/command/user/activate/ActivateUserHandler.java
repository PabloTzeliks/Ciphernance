package io.ciphernance.identity.application.command.user.activate;

import io.ciphernance.identity.application.exception.user.UserNotFoundException;
import io.ciphernance.identity.application.mediator.CommandHandler;
import io.ciphernance.identity.application.port.out.EventPublisherPort;
import io.ciphernance.identity.domain.event.AccessRestoredEvent;
import io.ciphernance.identity.domain.event.DomainEvent;
import io.ciphernance.identity.domain.event.UserStatusChangedEvent;
import io.ciphernance.identity.domain.model.User;
import io.ciphernance.identity.domain.port.UserRepositoryPort;
import io.ciphernance.identity.domain.vo.UserStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ActivateUserHandler implements CommandHandler<ActivateUserCommand, ActivateUserResponse> {

    private final UserRepositoryPort userRepository;
    private final EventPublisherPort eventPublisher;

    public ActivateUserHandler(UserRepositoryPort userRepository, EventPublisherPort eventPublisher) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }


    @Override
    public ActivateUserResponse handle(ActivateUserCommand command) {

        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));

        UserStatus previousStatus = user.getStatus();

        user.activate();

        userRepository.save(user);

        eventPublisher.publishAll(List.of(
                new UserStatusChangedEvent(
                        DomainEvent.newEventId(),
                        user.getId(),
                        previousStatus,
                        user.getStatus(),
                        user.getUpdatedAt()
                ),
                new AccessRestoredEvent(
                        DomainEvent.newEventId(),
                        user.getId(),
                        user.getUpdatedAt()
                )
        ));

        return new ActivateUserResponse(
                user.getId(),
                user.getUpdatedAt()
        );
    }
}
