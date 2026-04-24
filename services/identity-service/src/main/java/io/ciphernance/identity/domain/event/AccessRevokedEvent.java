package io.ciphernance.identity.domain.event;

import java.time.Instant;
import java.util.UUID;

public record AccessRevokedEvent(
        UUID eventId,
        UUID aggregateId,
        String reason,
        Instant occurredAt

) implements DomainEvent {

    @Override
    public String eventType() {
        return "ACCESS_REVOKED";
    }
}
