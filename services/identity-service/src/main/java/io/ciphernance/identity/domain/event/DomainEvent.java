package io.ciphernance.identity.domain.event;

import com.fasterxml.uuid.Generators;

import java.time.Instant;
import java.util.UUID;

public interface DomainEvent {

    UUID eventId();
    UUID aggregateId();
    Instant occurredAt();
    String eventType();

    static UUID newEventId() {
        return Generators.timeBasedEpochGenerator().generate();
    }
}
