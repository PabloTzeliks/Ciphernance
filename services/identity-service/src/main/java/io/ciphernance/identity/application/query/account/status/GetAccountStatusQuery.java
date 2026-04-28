package io.ciphernance.identity.application.query.account.status;

import io.ciphernance.identity.application.mediator.Query;
import io.ciphernance.identity.application.query.account.GetAccountResponse;

import java.util.UUID;

public record GetAccountStatusQuery(

        UUID accountId
) implements Query<GetAccountResponse> { }
