package io.ciphernance.identity.domain.event;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

import java.time.Instant;
import java.util.UUID;

public interface DomainEvent {

    UUID eventId();
    UUID aggregateId();
    Instant occurredAt();
    String eventType();

    TimeBasedEpochGenerator EVENT_ID_GENERATOR =
            Generators.timeBasedEpochGenerator();

    static UUID newEventId() {
        return EVENT_ID_GENERATOR.generate();
    }
}
