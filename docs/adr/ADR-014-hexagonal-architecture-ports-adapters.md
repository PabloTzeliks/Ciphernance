# ADR-014: Hexagonal Architecture — Ports and Adapters

## Status
Accepted

## Context
The Identity Service needed a clear structural boundary between business logic and infrastructure concerns. Without an explicit boundary, Spring annotations, JPA entities, and Kafka dependencies tend to leak into domain and application logic over time — making unit tests slow (requiring a Spring context), making the domain hard to reason about in isolation, and creating tight coupling to specific frameworks.

Two structural approaches were evaluated:

1. **Layered Architecture (traditional):** Presentation → Service → Repository. Simple, familiar, but typically results in service classes that depend directly on JPA repositories, making domain logic inseparable from persistence.
2. **Hexagonal Architecture (Ports and Adapters):** Business logic at the center defines interfaces (ports); infrastructure provides implementations (adapters). Dependencies point inward — never outward.

## Decision
Adopt **Hexagonal Architecture** with three explicit layers:

### Domain Layer (`domain/`)
Pure Java — no framework dependencies, no Spring annotations, no JPA, no Kafka.

Contains:
- **Entities:** `User`, `Account` — rich domain objects with behavior, not anemic data holders.
- **Value Objects:** `Email`, `Username`, `UserStatus`, `AccountStatus`, `KycLevel` — immutable, self-validating.
- **Domain Events:** `DomainEvent` interface + all concrete events (`UserRegisteredEvent`, `AccountCreatedEvent`, etc.).
- **Domain Exceptions:** invariant violations (`InvalidStatusTransitionException`, `MfaAlreadyEnabledException`, etc.).
- **Repository Ports:** `UserRepositoryPort`, `AccountRepositoryPort` — interfaces defined by the domain, implemented by infrastructure.

### Application Layer (`application/`)
Orchestration only — no framework dependencies. Depends on domain, but not on infrastructure.

Contains:
- **Command Handlers:** write use cases (`RegisterUserHandler`, `BlockUserHandler`, etc.).
- **Query Handlers:** read use cases (`GetUserProfileHandler`, `GetAccountStatusHandler`, etc.).
- **Mediator interfaces:** `Mediator`, `CommandHandler`, `QueryHandler`.
- **Output Ports:** interfaces for non-domain infrastructure concerns that handlers need — `EventPublisherPort`, `PasswordEncoderPort`, `TokenGeneratorPort`, `TotpGeneratorPort`, `TotpValidatorPort`, `MfaSetupCachePort`.
- **Application Exceptions:** use case failures (`UserNotFoundException`, `DuplicateEmailException`, `InvalidCredentialsException`, etc.).
- **DTOs:** `TokenClaims`, `TokenPair`.

### Infrastructure Layer (`infrastructure/`)
All framework dependencies live here. Implements the ports defined in domain and application layers.

Contains:
- **Persistence adapters:** JPA entities, Spring Data repositories, port implementations.
- **Messaging adapters:** Kafka publisher implementing `EventPublisherPort`; Kafka consumers for ABAC attribute events.
- **Security adapters:** BCrypt implementing `PasswordEncoderPort`; JWT implementing `TokenGeneratorPort`; TOTP library adapters.
- **Cache adapters:** Redis implementing `MfaSetupCachePort` and Policy Agent L2 cache.
- **Mediator implementation:** `MediatorImpl` — Spring `@Component`, wires all handlers at startup.
- **Policy Agent:** ABAC evaluation, embedded YAML policy loading, PDP/PIP.
- **Web adapters:** Spring MVC controllers, `GlobalExceptionHandler`.

### Dependency Rule
```
Infrastructure → Application → Domain
```
This direction is strictly enforced. Domain never imports from application or infrastructure. Application never imports from infrastructure.

## Consequences

**Gains:**
- Domain layer is testable in pure JUnit without loading a Spring context — entities and value objects are plain Java.
- Infrastructure can be swapped without touching domain or application logic (e.g., replacing PostgreSQL with another store requires only a new persistence adapter).
- Clear boundaries prevent coupling creep — a PR that adds a Spring annotation to a domain class is immediately visible as a violation.
- Forces explicit modeling of what is "business logic" vs "technical concern."

**Costs:**
- More classes per feature — domain entity, JPA entity, and mapping between them.
- Developers must know which layer owns each class. Violations are easy to make and require discipline to prevent.
- Output ports in the application layer add one level of indirection for infrastructure concerns that are straightforward in a layered architecture.
