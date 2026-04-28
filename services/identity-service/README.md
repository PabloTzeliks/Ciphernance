# Identity Service

The Identity Service is the IAM (Identity and Access Management) core of Ciphernance. It is the source of truth for user identities, account anchors, and dynamic ABAC attributes.

## Responsibilities

- User registration and authentication (JWT-based)
- Account identity anchor management (not financial state)
- KYC level management
- MFA setup and validation (TOTP)
- ABAC attribute distribution via Kafka events
- Policy evaluation for its own endpoints

## What this service does NOT own

- Wallet state or balances (owned by Wallet Service)
- Transaction processing (owned by Transaction Service)
- Fraud analysis (owned by Fraud Service)
- ABAC policy rules of other services (each service owns its own policies)

## Architecture

Hexagonal Architecture (Ports and Adapters) with CQS via Mediator Pattern.
See [ADR-009](../../docs/adr/ADR-009-cqrs-identity-service.md) and [ADR-014](../../docs/adr/ADR-014-hexagonal-architecture-ports-adapters.md) for details.

## Domain Model

```
User (1) → Account (1:1 in MVP, 1:N in future)
```

Account is an identity anchor only — no financial data. Financial state lives in Wallet Service.

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4 |
| Database | PostgreSQL 17 (via Flyway migrations) |
| Cache | Redis (MFA setup cache + ABAC attribute cache L2) |
| Messaging | Kafka (publishes domain events) |
| Security | Spring Security + JWT (manual, not Spring Authorization Server) |
| Build | Maven |

## Package Structure

```
io.ciphernance.identity/
├── domain/                  ← pure Java, no framework dependencies
│   ├── model/               ← User, Account
│   ├── vo/                  ← Email, Username, KycLevel, UserStatus, AccountStatus
│   ├── event/               ← DomainEvent interface + all domain events
│   ├── exception/           ← domain invariant violations
│   └── port/                ← UserRepositoryPort, AccountRepositoryPort
├── application/             ← orchestration only
│   ├── command/             ← Commands + Handlers (write operations)
│   ├── query/               ← Queries + Handlers (read operations)
│   ├── mediator/            ← Mediator interface, Command/QueryHandler interfaces
│   ├── port/out/            ← EventPublisherPort, TokenGeneratorPort, etc.
│   └── exception/           ← application-level exceptions
└── infrastructure/          ← all framework dependencies
    ├── persistence/         ← JPA entities, repositories, adapters
    ├── messaging/           ← Kafka publisher and consumers
    ├── security/            ← JWT, BCrypt, TOTP adapters
    ├── mediator/            ← MediatorImpl
    ├── policy/              ← Policy Agent, ABAC evaluation
    └── web/                 ← Controllers, GlobalExceptionHandler
```

## Running locally

Requires Docker Compose infrastructure running:

```bash
cd infrastructure
docker compose up -d
```

Then run the service:

```bash
cd services/identity-service
mvn spring-boot:run
```
