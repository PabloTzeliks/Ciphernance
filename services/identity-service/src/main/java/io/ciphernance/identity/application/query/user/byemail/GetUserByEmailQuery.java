package io.ciphernance.identity.application.query.user.byemail;

import io.ciphernance.identity.application.mediator.Query;
import io.ciphernance.identity.application.query.user.profile.GetUserProfileResponse;

public record GetUserByEmailQuery(

        String email
) implements Query<GetUserProfileResponse> { }
