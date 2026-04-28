# ADR-002: Eventually Consistent Authorization with local Policy Agents

## Status
Accepted

## Context
Ciphernance uses Attribute-Based Access Control (ABAC). Each authorization decision requires policies (what rules apply) and attributes (facts about the subject, resource, and environment). In a microservices system, there are two models for where this evaluation happens:

1. **Centralized PDP:** All services call a central authorization service at decision time (e.g., OPA, Keycloak Authorization Services).
2. **Distributed Policy Agents:** Each service embeds its own Policy Decision Point (PDP) with a local copy of policies and attributes.

A centralized PDP introduces network latency on every authorization call and creates a potential single point of failure in the critical path of every financial operation. For a system processing transactions, this is unacceptable.

## Decision
Each service embeds a **local Policy Agent** — a lightweight in-process component composed of:
- **PDP (PolicyEngine):** Evaluates policies against collected attributes locally.
- **PIP (AttributeRepository):** Fetches and caches subject/resource/environment attributes.
- **Local policy store:** An in-memory copy of compiled policies, kept fresh via Kafka.

**Identity Service** is the single source of truth (PAP). It compiles YAML policy definitions at startup and distributes them via Kafka (`policy-updates` topic). It also publishes specific domain attribute events when user or account state changes — `UserStatusChangedEvent`, `AccountStatusChangedEvent`, `KycLevelUpdatedEvent`, `MfaEnabledEvent` — consumed by each service's Policy Agent to update its local attribute cache.

Each Policy Agent consumes these events and updates its local state. This makes authorization **eventually consistent**: there is a brief window after a policy or attribute change during which a service may still use the previous value. This is accepted as a deliberate trade-off.

**Exception — revocation:** Access revocation is treated as a special case (see ADR-010). `AccessRevokedEvent` flows through a dedicated high-priority topic and triggers immediate cache invalidation.

## Consequences

**Benefits:**
- Zero network calls on the authorization hot path — decisions are made in-process.
- No single point of failure for authorization.
- Each service can extend its local PIP with domain-specific attributes (e.g., Wallet Service knows wallet balance and status).

**Trade-offs:**
- Policy updates are not instantaneous across services — eventual consistency window exists.
- Each service carries the operational overhead of consuming and applying policy/attribute events.
- Debugging authorization failures may require checking which policy version a specific service instance has loaded.

## Cold Start and Drift Recovery

On startup, each service consumes a `FullSyncEvent` from the `full-sync` Kafka topic. This event contains all current user attributes published by Identity Service and is used to warm the Policy Agent's attribute cache before the service begins accepting requests.

If Kafka is unavailable on startup, the service falls back to L2 Redis cache (see ADR-005). Redis holds the last known attribute state with a longer TTL specifically to cover restart scenarios. If both Kafka and Redis are unavailable, the service starts with an empty attribute cache and builds it incrementally as domain events arrive — accepting the risk of briefly evaluating decisions with incomplete attributes.
