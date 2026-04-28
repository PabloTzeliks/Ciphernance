# Architectural Decision Records

This directory contains all ADRs for the Ciphernance project. Each ADR documents a significant architectural decision, its context, and its trade-offs.

| ADR | Title | Status |
|-----|-------|--------|
| [ADR-001](ADR-001-choreography-saga.md) | Choreography-based Saga for distributed transactions | Accepted |
| [ADR-002](ADR-002-eventually-consistent-authorization.md) | Eventually Consistent Authorization with local Policy Agents | Accepted |
| [ADR-003](ADR-003-yaml-dsl-abac-policies.md) | YAML DSL for ABAC policy definitions | Accepted |
| [ADR-004](ADR-004-event-sourcing-transaction-service.md) | Event Sourcing scoped to Transaction Service | Accepted |
| [ADR-005](ADR-005-two-level-cache.md) | Two-level cache (L1 Caffeine + L2 Redis) for Policy Agents | Accepted |
| [ADR-006](ADR-006-no-decision-cache-financial.md) | No decision-level cache in financial authorization | Accepted |
| [ADR-007](ADR-007-uuid-v7-strategy.md) | UUID v7 as Primary Key Strategy | Accepted |
| [ADR-008](ADR-008-domain-model-user-account-wallet-balance.md) | Domain Model — User, Account, Wallet, Balance | Accepted |
| [ADR-009](ADR-009-cqrs-identity-service.md) | CQS (Command Query Separation) for Identity Service | Accepted |
| [ADR-010](ADR-010-jwt-blocklist-redis.md) | JWT Session Invalidation via Redis Blocklist | Accepted |
| [ADR-013](ADR-013-abac-policy-distribution-strategy.md) | ABAC Policy Distribution Strategy | Accepted |
| [ADR-014](ADR-014-hexagonal-architecture-ports-adapters.md) | Hexagonal Architecture — Ports and Adapters | Accepted |
| [ADR-015](ADR-015-exception-hierarchy-domain-vs-application.md) | Exception Hierarchy — Domain vs Application | Accepted |
