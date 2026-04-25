package io.ciphernance.identity.application.command.user.authenticate.login;

import io.ciphernance.identity.application.dto.TokenClaims;
import io.ciphernance.identity.application.dto.TokenPair;
import io.ciphernance.identity.application.exception.user.UserInactiveException;
import io.ciphernance.identity.application.exception.account.AccountNotFoundException;
import io.ciphernance.identity.application.exception.user.InvalidCredentialsException;
import io.ciphernance.identity.application.exception.user.InvalidMfaCodeException;
import io.ciphernance.identity.application.exception.user.MfaRequiredException;
import io.ciphernance.identity.application.mediator.CommandHandler;
import io.ciphernance.identity.application.port.out.PasswordEncoderPort;
import io.ciphernance.identity.application.port.out.TokenGeneratorPort;
import io.ciphernance.identity.application.port.out.TotpValidatorPort;
import io.ciphernance.identity.domain.model.Account;
import io.ciphernance.identity.domain.model.User;
import io.ciphernance.identity.domain.model.enums.AuthLevel;
import io.ciphernance.identity.domain.port.AccountRepositoryPort;
import io.ciphernance.identity.domain.port.UserRepositoryPort;
import io.ciphernance.identity.domain.vo.Email;
import io.ciphernance.identity.domain.vo.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class LoginUserHandler implements CommandHandler<LoginUserCommand, LoginUserResponse> {

    private final UserRepositoryPort userRepository;
    private final AccountRepositoryPort accountRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenGeneratorPort tokenGenerator;
    private final TotpValidatorPort totpValidator;

    public LoginUserHandler(UserRepositoryPort userRepository,
                            AccountRepositoryPort accountRepository,
                            PasswordEncoderPort passwordEncoder,
                            TokenGeneratorPort tokenGenerator,
                            TotpValidatorPort totpValidator) {

        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
        this.totpValidator = totpValidator;
    }

    @Override
    public LoginUserResponse handle(LoginUserCommand command) {

        User user = userRepository.findByEmail(new Email(command.email()))
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UserInactiveException(user.getStatus());
        }

        if (user.isMfaEnabled()) {
            if (command.totpCode() == null) {
                String pendingToken = tokenGenerator.generateMfaPending(user.getId());
                throw new MfaRequiredException(pendingToken);
            }
            if (!totpValidator.validate(user.getMfaSecret(), command.totpCode())) {
                throw new InvalidMfaCodeException();
            }
        }

        Account account = accountRepository.findByOwnerId(user.getId())
                .orElseThrow(() -> new AccountNotFoundException(user.getId()));

        AuthLevel authLevel = user.isMfaEnabled() ? AuthLevel.MFA : AuthLevel.BASIC;

        TokenPair tokens = tokenGenerator.generate(new TokenClaims(
                user.getId(),
                user.getRole().name(),
                user.getKycLevel().name(),
                account.getId(),
                account.getStatus().name(),
                user.isMfaEnabled(),
                authLevel
        ));

        return new LoginUserResponse(
                tokens.accessToken(),
                tokens.refreshToken(),
                "Bearer",
                tokens.expiresIn()
        );
    }
}
