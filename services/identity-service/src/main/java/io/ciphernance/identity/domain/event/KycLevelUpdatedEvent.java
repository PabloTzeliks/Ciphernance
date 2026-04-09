package io.ciphernance.identity.domain.event;

import io.ciphernance.identity.domain.vo.KycLevel;

import java.time.Instant;
import java.util.UUID;

public record KycLevelUpdatedEvent(
        UUID eventId,
        UUID aggregateId,
        KycLevel previousLevel,
        KycLevel newLevel,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String eventType() {
        return "KYC_LEVEL_UPDATED";
    }
}
