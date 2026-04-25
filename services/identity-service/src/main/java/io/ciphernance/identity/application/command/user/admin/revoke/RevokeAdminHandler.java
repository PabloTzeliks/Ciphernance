package io.ciphernance.identity.application.command.user.admin.revoke;

import io.ciphernance.identity.application.exception.user.UserNotFoundException;
import io.ciphernance.identity.application.mediator.CommandHandler;
import io.ciphernance.identity.application.port.out.EventPublisherPort;
import io.ciphernance.identity.domain.event.DomainEvent;
import io.ciphernance.identity.domain.event.UserRoleChangedEvent;
import io.ciphernance.identity.domain.model.User;
import io.ciphernance.identity.domain.model.enums.UserRole;
import io.ciphernance.identity.domain.port.UserRepositoryPort;
import org.springframework.stereotype.Component;

@Component
public class RevokeAdminHandler implements CommandHandler<RevokeAdminCommand, Void> {

    private final UserRepositoryPort userRepository;
    private final EventPublisherPort eventPublisher;

    public RevokeAdminHandler(UserRepositoryPort userRepository, EventPublisherPort eventPublisher) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Void handle(RevokeAdminCommand command) {

        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));

        UserRole previousRole = user.getRole();

        user.revokeAdmin();

        userRepository.save(user);

        eventPublisher.publish(
                new UserRoleChangedEvent(
                        DomainEvent.newEventId(),
                        user.getId(),
                        previousRole,
                        user.getRole(),
                        user.getUpdatedAt()
                )
        );

        return null;
    }
}
