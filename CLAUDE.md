# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

# Ciphernance

Core banking engine built intentionally with overengineering for learning distributed systems architecture. Not a product — a deliberate learning platform.

## What This Is

Ciphernance emulates the core of a banking system focused on transactions, authorization, and governance. Built with Java 21, Spring Boot 4, Kafka, PostgreSQL, Redis, Neo4J, and ABAC.

## Services

- **api-gateway** — Entry point. JWT validation, ABAC first layer (PEP), rate limiting, routing. Spring Cloud Gateway.
- **identity-service** — Source of truth for identities, accounts, and authorization policies. Spring Authorization Server. Manages PAP (YAML policies), PIP (user/account attributes). Compiles YAML policies and distributes via Kafka. Owns User and Account domain entities.
- **wallet-service** — Wallet and balance management. Owns Wallet and Balance domain entities. Creates Wallet upon AccountCreatedEvent from identity-service. Validates balance, reserves funds during Saga, finalizes debit/credit after confirmation. Blocks wallets on fraud detection.
- **transaction-service** — Orchestrates the transfer Saga. ABAC authorization (ownership validation). Event Sourcing for transaction state. Choreography-based Saga coordinator.
- **fraud-service** — Pattern analysis. Redis for fast rules. Neo4J for relationship graphs (transfer cycles, shared devices). Publishes CLEARED or SUSPECTED verdict.
- **audit-service** — Immutable event history. Consumes authorization audit events and business events from all services. Never affects main flow latency.

## Architecture Decisions

- **Saga Pattern:** Choreography-based (no orchestrator). Each service reacts to events and publishes new events.
- **ABAC:** Eventually Consistent Authorization. Each service has a local Policy Agent with L1 Caffeine cache + L2 Redis cache. Identity Service is the source of truth — distributes policies and attributes via Kafka events. No decision-level cache (financial system — always re-evaluate).
- **ABAC Architecture:** XACML-inspired. PAP (Identity Service), PIP (AttributeRepository per service), PDP (PolicyEngine per service), PEP (AbacAuthorizationManager in Spring Security).
- **Policies:** YAML DSL versioned in /policies directory. Compiled by Identity Service at startup. Distributed via Kafka as PolicyUpdatedEvent.
- **Event Sourcing:** Transaction Service only. Transaction state = sum of all events it generated.
- **Revocation:** Immediate. AccessRevokedEvent has its own high-priority Kafka topic. Policy Agents invalidate L1 and L2 cache immediately on consumption.

## Kafka Topics

**ABAC Sync:**
- policy-updates
- attribute-changes
- access-revocation (high priority)
- full-sync (periodic)

**Transaction Saga:**
- transaction-initiated
- accounts-validated
- account-validation-failed
- transaction-approved-for-analysis
- transaction-cleared
- fraud-suspected
- transaction-completed
- transaction-reversed

**Audit:**
- authorization-audit
- business-events-audit

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4 |
| API Gateway | Spring Cloud Gateway |
| IAM | Spring Authorization Server + JWT |
| Messaging | Kafka + Zookeeper |
| Database | PostgreSQL (Identity, Account, Transaction, Audit) |
| Graph | Neo4J (Fraud Service) |
| Cache | Redis per service (L2 Policy Agent) |
| L1 Cache | Caffeine in-memory per service |
| Policies | YAML DSL in /policies directory |
| Containers | Docker Compose |
| Observability | Micrometer + Prometheus + Grafana |
| Tests | JUnit 5 + Testcontainers |

## Transfer Saga Flow

1. Client → API Gateway (JWT validated)
2. API Gateway → Transaction Service
3. Transaction Service: ABAC check (ownership + permission) → publishes TransactionInitiatedEvent → state: PENDING
4. Wallet Service: validates balance + wallet state → reserves funds → publishes AccountsValidatedEvent or AccountValidationFailedEvent
5. Transaction Service: publishes TransactionApprovedForAnalysisEvent
6. Fraud Service: analyzes patterns → publishes TransactionClearedEvent or FraudSuspectedEvent
7a. CLEARED → Transaction Service publishes TransactionCompletedEvent → Wallet Service finalizes debit/credit
7b. SUSPECTED → Transaction Service publishes TransactionReversalEvent → Wallet Service releases reserved funds + blocks wallets → Identity Service publishes AccessRevokedEvent

## ADRs

See /docs/adr/ for all Architectural Decision Records.

- ADR-001: Choreography-based Saga
- ADR-002: Eventually Consistent Authorization with local Policy Agents
- ADR-003: YAML DSL for ABAC policies
- ADR-004: Event Sourcing in Transaction Service
- ADR-005: Two-level cache L1 Caffeine + L2 Redis
- ADR-006: No decision-level cache in financial systems
- ADR-007: UUID v7 as Primary Key Strategy
- ADR-008: Domain Model — User, Account, Wallet, Balance

## Git Workflow

- NEVER add Co-Authored-By in commit messages
- NEVER commit directly to main branch
- Always create a feature branch before making changes: git checkout -b <branch-name>
- Branch naming convention:
  - feature/description — new features
  - chore/description — setup, config, infra
  - fix/description — bug fixes
  - docs/description — documentation only
- Always push the branch and let the developer create the PR manually
- Commit messages must follow Conventional Commits: type(scope): description

## Development Notes

- Each service has its own independent pom.xml
- Services communicate exclusively via Kafka (async) — no direct REST calls between services
- REST only for client-facing endpoints (API Gateway inbound)
- Each service has its own PostgreSQL schema
- Policy Agent is embedded in each service JAR — not a separate service
- Out of scope for MVP: deposits, withdrawals, credit, investments, card, real Banco Central integration, Kubernetes, frontend

## Implementation Notes

### identity-service domain layer

**Package convention for enums:**
Two categories coexist in the domain:
- Enums with behavioral methods (state machine transitions, promotion logic) live in `domain/vo/`: `UserStatus`, `AccountStatus`, `KycLevel`
- Simple classification enums without behavior live in `domain/model/enums/`: `UserRole`, `AccountType`

**`User.create()` accepts pre-constructed Value Objects, not raw Strings.**
The factory signature is `User.create(Username, Email, String passwordHash)`. VO construction (`new Email(...)`, `new Username(...)`) is the caller's (use case's) responsibility. The ADR-007 example showed raw Strings for illustration only — the actual implementation is intentionally different.

**`Account` uses `ownerId` instead of `userId`.**
The entity field and port methods use `ownerId` to remain forward-compatible with ADR-008's deferred `OwnerType` (USER, AGENT, MERCHANT). MVP only supports USER, but the naming is intentional.

**`KycLevel.isEligibleForAmount(BigDecimal)` is deliberately absent.**
Amount limits are owned by ABAC policies (transfer-policies.yml), not the domain model. Placing limit enforcement in the domain would duplicate and potentially diverge from the policy engine. KycLevel carries only promotion logic — authorization decisions belong to the PDP.
