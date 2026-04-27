package io.ciphernance.identity.application.command.user.status.block;

import io.ciphernance.identity.application.exception.user.UserNotFoundException;
import io.ciphernance.identity.application.mediator.CommandHandler;
import io.ciphernance.identity.application.port.out.EventPublisherPort;
import io.ciphernance.identity.domain.event.AccessRevokedEvent;
import io.ciphernance.identity.domain.event.AccountStatusChangedEvent;
import io.ciphernance.identity.domain.event.DomainEvent;
import io.ciphernance.identity.domain.event.UserStatusChangedEvent;
import io.ciphernance.identity.domain.model.Account;
import io.ciphernance.identity.domain.model.User;
import io.ciphernance.identity.domain.port.AccountRepositoryPort;
import io.ciphernance.identity.domain.port.UserRepositoryPort;
import io.ciphernance.identity.domain.vo.AccountStatus;
import io.ciphernance.identity.domain.vo.UserStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BlockUserHandler implements CommandHandler<BlockUserCommand, Void> {

    private final UserRepositoryPort userRepository;
    private final AccountRepositoryPort accountRepository;
    private final EventPublisherPort eventPublisher;

    public BlockUserHandler(UserRepositoryPort userRepository,
                            AccountRepositoryPort accountRepository,
                            EventPublisherPort eventPublisher) {

        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Void handle(BlockUserCommand command) {

        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));

        UserStatus previousUserStatus = user.getStatus();

        user.block();

        Account account = accountRepository.findByOwnerId(user.getId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));

        AccountStatus previousAccountStatus = account.getStatus();

        account.block();

        userRepository.save(user);

        eventPublisher.publishAll(List.of(
                new UserStatusChangedEvent(
                        DomainEvent.newEventId(),
                        user.getId(),
                        previousUserStatus,
                        user.getStatus(),
                        user.getUpdatedAt()
                ),
                new AccountStatusChangedEvent(
                        DomainEvent.newEventId(),
                        user.getId(),
                        user.getId(),
                        previousAccountStatus,
                        account.getStatus(),
                        account.getUpdatedAt()
                ),
                new AccessRevokedEvent(
                        DomainEvent.newEventId(),
                        user.getId(),
                        account.getId(),
                        user.getUpdatedAt()
                )
        ));

        return null;
    }
}
