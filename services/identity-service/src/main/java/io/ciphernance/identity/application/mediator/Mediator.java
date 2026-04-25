package io.ciphernance.identity.application.mediator;

public interface Mediator {
    <R> R send(Command<R> command);
    <R> R query(Query<R> query);
}
