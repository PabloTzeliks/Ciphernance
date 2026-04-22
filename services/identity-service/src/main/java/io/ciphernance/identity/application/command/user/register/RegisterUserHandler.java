package io.ciphernance.identity.application.command.user.register;

import io.ciphernance.identity.application.exception.user.DuplicateEmailException;
import io.ciphernance.identity.application.exception.user.DuplicateUsernameException;
import io.ciphernance.identity.application.mediator.CommandHandler;
import io.ciphernance.identity.application.port.out.EventPublisherPort;
import io.ciphernance.identity.application.port.out.PasswordEncoderPort;
import io.ciphernance.identity.domain.event.AccountCreatedEvent;
import io.ciphernance.identity.domain.event.DomainEvent;
import io.ciphernance.identity.domain.event.UserRegisteredEvent;
import io.ciphernance.identity.domain.model.Account;
import io.ciphernance.identity.domain.model.User;
import io.ciphernance.identity.domain.port.AccountRepositoryPort;
import io.ciphernance.identity.domain.port.UserRepositoryPort;
import io.ciphernance.identity.domain.vo.Email;
import io.ciphernance.identity.domain.vo.Username;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RegisterUserHandler implements CommandHandler<RegisterUserCommand, RegisterUserResponse> {

    private final UserRepositoryPort userRepository;
    private final AccountRepositoryPort accountRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final EventPublisherPort eventPublisher;

    public RegisterUserHandler(UserRepositoryPort userRepository,
                               AccountRepositoryPort accountRepository,
                               PasswordEncoderPort passwordEncoder,
                               EventPublisherPort eventPublisher) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public RegisterUserResponse handle(RegisterUserCommand command) {

        if (userRepository.existsByEmail(new Email(command.email()))) {
            throw new DuplicateEmailException(command.email());
        }

        if (userRepository.existsByUsername(new Username(command.username()))) {
            throw new DuplicateUsernameException(command.username());
        }

        User user = User.create(
                new Username(command.username()),
                new Email(command.email()),
                passwordEncoder.encode(command.password())
        );

        Account account = Account.createFor(user.getId());

        userRepository.save(user);
        accountRepository.save(account);

        eventPublisher.publishAll(List.of(
                new UserRegisteredEvent(
                        DomainEvent.newEventId(),
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getCreatedAt()
                ),
                new AccountCreatedEvent(
                        DomainEvent.newEventId(),
                        account.getId(),
                        account.getOwnerId(),
                        account.getType(),
                        account.getStatus(),
                        account.getCreatedAt()
                )
        ));

        return new RegisterUserResponse(
                user.getId(),
                account.getId(),
                user.getUsername().value(),
                user.getEmail().value(),
                user.getCreatedAt()
        );
    }
}
