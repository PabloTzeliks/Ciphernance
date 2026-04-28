package io.ciphernance.identity.application.query.user.profile;

import io.ciphernance.identity.application.exception.user.UserNotFoundException;
import io.ciphernance.identity.application.mediator.QueryHandler;
import io.ciphernance.identity.domain.model.User;
import io.ciphernance.identity.domain.port.UserRepositoryPort;
import org.springframework.stereotype.Component;

@Component
public class GetUserProfileHandler implements QueryHandler<GetUserProfileQuery, GetUserProfileResponse> {

    private final UserRepositoryPort userRepository;

    public GetUserProfileHandler(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public GetUserProfileResponse handle(GetUserProfileQuery query) {

        User user = userRepository.findById(query.userId())
                .orElseThrow(() -> new UserNotFoundException(query.userId()));

        return GetUserProfileResponse.from(user);
    }
}
