# ADR-009: CQS (Command Query Separation) for Identity Service

## Status
Accepted

## Context
The Identity Service handles two distinct types of operations: commands that modify state (RegisterUser, BlockUser, PromoteKyc) and queries that read state (GetUserProfile, GetAccountStatus).

A decision was needed on whether to apply command/query separation and at what level — basic semantic separation vs advanced read/write model separation with dedicated databases.

Additionally, the proliferation of Command Handlers would create controllers with many injected dependencies, requiring a dispatch mechanism.

## Decision

### CQS (Command Query Separation) — applied to Identity Service

This is CQS, not CQRS. CQRS (Command Query Responsibility Segregation) implies physically separate read and write models, typically with different databases or projections. CQS here means Commands and Queries are handled by separate classes via Mediator, but both use the same PostgreSQL database.

Separate Commands (write operations) from Queries (read operations) at the structural and semantic level only. Both use the same PostgreSQL database. No separate read model or projections.

Rationale: The Identity Service has balanced and simple read/write patterns. No read performance bottleneck justifies a separate read model. The value here is semantic clarity and learning the pattern in a simpler context before applying it fully in Transaction Service.

### CQRS — reserved for Transaction Service
Event Sourcing in Transaction Service requires genuine read/write separation: the write side appends events to an EventStore, the read side serves projections. CQRS is a necessity there, not a choice.

### Mediator Pattern
A Mediator dispatches Commands and Queries to their respective Handlers. Controllers depend only on the Mediator — not on individual Handlers. This keeps controllers thin and decoupled from application logic.

Type-safe generic interface:
```java
public interface Mediator {
    <R> R send(Command<R> command);
    <R> R query(Query<R> query);
}
```

### Application Layer Structure
```
application/
├── command/
│   └── user/
│       ├── authenticate/
│       │   ├── login/
│       │   └── register/
│       ├── admin/
│       │   ├── kyc/
│       │   ├── promote/
│       │   └── revoke/
│       ├── mfa/
│       │   ├── confirm/
│       │   ├── disable/
│       │   └── setup/
│       └── status/
│           ├── activate/
│           ├── block/
│           └── suspend/
├── query/
│   ├── user/
│   │   ├── profile/
│   │   └── byemail/
│   └── account/
│       ├── status/
│       └── byownerid/
├── mediator/
│   ├── Mediator.java
│   ├── Command.java
│   ├── CommandHandler.java
│   ├── Query.java
│   └── QueryHandler.java
└── port/out/
    ├── EventPublisherPort.java
    ├── PasswordEncoderPort.java
    ├── TokenGeneratorPort.java
    ├── TotpGeneratorPort.java
    ├── TotpValidatorPort.java
    └── MfaSetupCachePort.java
```

Account Commands are deferred — Account state changes are driven by User status commands that cascade internally within the same handler. There is no standalone `command/account/` package.

Event Handlers for external Kafka events (from other services) belong in the infrastructure layer, not in the application layer. The application layer only defines commands, queries, and output ports.

*Implementation of Mediator lives in infrastructure (`infrastructure/mediator/MediatorImpl`).*

## Consequences

**Gains:**
- Semantic clarity — commands and queries are explicitly separated
- Controllers depend only on Mediator — thin and testable
- Learning CQS before full CQRS in Transaction Service
- Adding new commands/queries requires no changes to existing code — open/closed

**Costs:**
- More classes per operation — Command + Handler per use case
- Mediator adds indirection — harder to trace execution flow at first glance
- CQS does not solve read performance — same database for reads and writes

## What this is NOT
- CQRS with separate read/write databases in Identity Service
- Event Sourcing in Identity Service
- Axon Framework or any CQRS framework — implemented manually for learning purposes
