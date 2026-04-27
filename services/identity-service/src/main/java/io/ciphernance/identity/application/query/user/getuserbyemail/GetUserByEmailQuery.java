package io.ciphernance.identity.application.query.user.getuserbyemail;

import io.ciphernance.identity.application.mediator.Query;
import io.ciphernance.identity.application.query.user.getuserprofile.GetUserProfileResponse;

public record GetUserByEmailQuery(

        String email
) implements Query<GetUserProfileResponse> { }
