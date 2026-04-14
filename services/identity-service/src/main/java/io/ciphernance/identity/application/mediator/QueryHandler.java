package io.ciphernance.identity.application.mediator;

public interface QueryHandler<Q extends Query<R>, R> {
    R handle(Q query);
}
