package io.ciphernance.identity.domain.event;

import io.ciphernance.identity.domain.vo.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatusChangedEvent(
        UUID eventId,
        UUID aggregateId,
        UserStatus previousStatus,
        UserStatus newStatus,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String eventType() {
        return "USER_STATUS_CHANGED";
    }
}
