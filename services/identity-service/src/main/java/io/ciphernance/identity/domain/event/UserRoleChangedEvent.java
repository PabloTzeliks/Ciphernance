package io.ciphernance.identity.domain.event;

import io.ciphernance.identity.domain.model.enums.UserRole;

import java.time.Instant;
import java.util.UUID;

public record UserRoleChangedEvent(
        UUID eventId,
        UUID aggregateId,
        UserRole newRole,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String eventType() {
        return "USER_ROLE_CHANGED";
    }
}
