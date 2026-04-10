package io.ciphernance.identity.domain.event;

import io.ciphernance.identity.domain.model.enums.AccountType;
import io.ciphernance.identity.domain.vo.AccountStatus;

import java.time.Instant;
import java.util.UUID;

public record AccountCreatedEvent(
        UUID eventId,
        UUID aggregateId,
        UUID ownerId,
        AccountType type,
        AccountStatus status,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String eventType() {
        return "ACCOUNT_CREATED";
    }
}
