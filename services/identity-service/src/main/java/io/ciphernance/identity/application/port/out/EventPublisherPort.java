package io.ciphernance.identity.application.port.out;

import io.ciphernance.identity.domain.event.DomainEvent;

import java.util.List;

public interface EventPublisherPort {

    void publish(DomainEvent event);
    void publishAll(List<DomainEvent> events);
}
