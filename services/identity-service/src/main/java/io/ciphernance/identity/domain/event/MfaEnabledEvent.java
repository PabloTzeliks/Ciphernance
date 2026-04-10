package io.ciphernance.identity.domain.event;

import java.time.Instant;
import java.util.UUID;

public record MfaEnabledEvent(
        UUID eventId,
        UUID aggregateId,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String eventType() {
        return "MFA_ENABLED";
    }
}
