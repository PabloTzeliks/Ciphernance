package io.ciphernance.identity.application.query.user.profile;

import io.ciphernance.identity.application.mediator.Query;
import io.ciphernance.identity.application.query.user.GetUserProfileResponse;

import java.util.UUID;

public record GetUserProfileQuery(

        UUID userId
) implements Query<GetUserProfileResponse> { }
