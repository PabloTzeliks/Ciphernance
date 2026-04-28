package io.ciphernance.identity.application.query.account.byownerid;

import io.ciphernance.identity.application.mediator.Query;
import io.ciphernance.identity.application.query.account.GetAccountResponse;

import java.util.UUID;

public record GetAccountByOwnerIdQuery(

        UUID ownerId
) implements Query<GetAccountResponse> { }
