package io.ciphernance.identity.domain.event;

import java.time.Instant;
import java.util.UUID;

public record AccessRestoredEvent(

        UUID eventId,
        UUID aggregateId,
        UUID accountId,
        Instant occurredAt

) implements DomainEvent {

    @Override
    public String eventType() {
        return "ACCESS_RESTORED";
    }
}
