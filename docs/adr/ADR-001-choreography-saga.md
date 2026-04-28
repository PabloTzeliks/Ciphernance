# ADR-001: Choreography-based Saga for distributed transactions

## Status
Accepted

## Context
Ciphernance must execute multi-step financial transactions (transfer Saga) that span multiple services: Transaction Service, Wallet Service, Fraud Service, and Identity Service. Each step must either complete successfully or trigger a full compensating rollback. In a microservices architecture, there is no shared database transaction — consistency must be achieved through coordination.

Two Saga implementation styles exist: **orchestration** (a central coordinator issues commands to each participant) and **choreography** (each service reacts to events and publishes new events, with no central controller).

The choice directly impacts coupling, operational complexity, and the learning objectives of the project.

## Decision
Adopt **choreography-based Saga**. Each service listens to Kafka topics, reacts to domain events, executes its local transaction, and publishes the outcome as a new event. There is no dedicated Saga orchestrator service. The Transaction Service acts as the logical initiator and terminator of the Saga but does not issue commands to other services — it publishes events they react to.

Compensation (rollback) is also event-driven: a `FraudSuspectedEvent` triggers a `TransactionReversalEvent`, which Wallet Service listens to in order to release reserved funds.

## Consequences

**Benefits:**
- Services are fully decoupled — each service only knows about Kafka topics, not about other services.
- No single point of failure introduced by an orchestrator.
- Scales horizontally without a bottleneck coordinator.
- Forces explicit domain event modeling, which is valuable for learning.

**Trade-offs:**
- The overall Saga flow is implicit and spread across multiple services, making it harder to observe end-to-end without dedicated tracing.
- Debugging failed Sagas requires correlating events across multiple Kafka topics by `correlationId`.
- Adding new Saga steps requires modifying multiple services and topics, not a single orchestrator.
- Cyclic event dependencies must be carefully avoided during design.
