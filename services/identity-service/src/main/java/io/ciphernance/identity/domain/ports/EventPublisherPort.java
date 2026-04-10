package io.ciphernance.identity.domain.ports;

import io.ciphernance.identity.domain.event.DomainEvent;

import java.util.List;

public interface EventPublisherPort {

    void publish(DomainEvent event);
    void publishAll(List<DomainEvent> events);
}
