package io.ciphernance.identity.application.query.user.profile;

import io.ciphernance.identity.application.mediator.Query;

import java.util.UUID;

public record GetUserProfileQuery(

        UUID userId
) implements Query<GetUserProfileResponse> { }
