# ADR-004: Event Sourcing scoped to Transaction Service

## Status
Accepted

## Context
Financial transactions have a lifecycle: PENDING → APPROVED_FOR_ANALYSIS → COMPLETED or REVERSED. Each state transition is triggered by an external event (from Account Service or Fraud Service). The state of a transaction at any point must be fully reconstructable and auditable.

Two persistence models were considered:
1. **State-based persistence:** Store the current state of each transaction row in PostgreSQL, updating it as events arrive.
2. **Event Sourcing:** Store every event that affected a transaction; current state is derived by replaying the event log.

Event Sourcing is a natural fit for Transaction Service because the business domain is inherently event-driven (each Kafka event IS the state change), and financial auditability benefits greatly from an immutable event log.

However, applying Event Sourcing to every service would add significant complexity with little benefit in services like Account Service (which primarily manages mutable balance state) or Identity Service (which manages relatively stable identity records).

## Decision
Apply **Event Sourcing exclusively to Transaction Service**. The transaction event store is a PostgreSQL table (`transaction_events`) where each row is an immutable domain event with a `transaction_id`, `event_type`, `payload` (JSON), `sequence_number`, and `occurred_at`. The current state of a transaction is always computed by replaying its event stream.

No other service uses Event Sourcing. Account Service, Identity Service, Fraud Service, and Audit Service use conventional state-based PostgreSQL persistence.

Audit Service receives the same domain events via Kafka and stores them in its own append-only log for cross-service auditability — this is not Event Sourcing, just event storage for audit purposes.

## Consequences

**Benefits:**
- Complete, immutable audit trail for every transaction state transition — each event has a precise timestamp and cause.
- Replaying events can reconstruct transaction state at any point in time.
- Natural alignment with the choreography Saga: each Kafka event received by Transaction Service directly maps to an event appended to the store.
- Strong learning value: forces understanding of projection, replay, and event schema evolution.

**Trade-offs:**
- Querying current transaction state requires a projection or a read model — cannot simply `SELECT * FROM transactions WHERE id = ?`.
- Event schema evolution (adding/removing fields) requires migration strategies (upcasting, versioned events).
- Replay time grows as event history accumulates — snapshots may eventually be needed.
- Developers unfamiliar with Event Sourcing face a steeper onboarding curve for Transaction Service specifically.
