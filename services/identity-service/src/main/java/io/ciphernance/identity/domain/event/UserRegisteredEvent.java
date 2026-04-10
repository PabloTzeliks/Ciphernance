package io.ciphernance.identity.domain.event;

import io.ciphernance.identity.domain.vo.Email;
import io.ciphernance.identity.domain.vo.Username;

import java.time.Instant;
import java.util.UUID;

public record UserRegisteredEvent(
        UUID eventId,
        UUID aggregateId,
        Username username,
        Email email,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String eventType() {
        return "USER_REGISTERED";
    }
}
