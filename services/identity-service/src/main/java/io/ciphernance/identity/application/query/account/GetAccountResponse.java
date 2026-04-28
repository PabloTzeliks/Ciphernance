package io.ciphernance.identity.application.query.account;

import io.ciphernance.identity.domain.model.Account;

import java.time.Instant;
import java.util.UUID;

public record GetAccountResponse(

        UUID id,
        UUID ownerId,
        String type,
        String status,
        Instant createdAt,
        Instant updatedAt
) {

    public static GetAccountResponse from(Account account) {

        return new GetAccountResponse(
                account.getId(),
                account.getOwnerId(),
                account.getType().name(),
                account.getStatus().name(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }
}
