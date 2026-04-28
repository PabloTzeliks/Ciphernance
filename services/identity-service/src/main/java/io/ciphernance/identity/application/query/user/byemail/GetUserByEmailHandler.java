package io.ciphernance.identity.application.query.user.byemail;

import io.ciphernance.identity.application.exception.user.UserNotFoundException;
import io.ciphernance.identity.application.mediator.QueryHandler;
import io.ciphernance.identity.application.query.user.GetUserProfileResponse;
import io.ciphernance.identity.domain.model.User;
import io.ciphernance.identity.domain.port.UserRepositoryPort;
import io.ciphernance.identity.domain.vo.Email;
import org.springframework.stereotype.Component;

@Component
public class GetUserByEmailHandler implements QueryHandler<GetUserByEmailQuery, GetUserProfileResponse> {

    private final UserRepositoryPort userRepository;

    public GetUserByEmailHandler(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public GetUserProfileResponse handle(GetUserByEmailQuery query) {

        Email email = new Email(query.email());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> UserNotFoundException.forEmail(query.email()));

        return GetUserProfileResponse.from(user);
    }
}
