package io.ciphernance.identity.application.query.user.getuserprofile;

import io.ciphernance.identity.application.mediator.Query;

import java.util.UUID;

public record GetUserProfileQuery(

        UUID userId
) implements Query<GetUserProfileResponse> { }
