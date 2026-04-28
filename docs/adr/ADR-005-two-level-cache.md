# ADR-005: Two-level cache (L1 Caffeine + L2 Redis) for Policy Agents

## Status
Accepted

## Context
Each service's Policy Agent needs fast access to two types of data:
1. **Compiled policies** — infrequently changing, used on every authorization decision.
2. **Subject and resource attributes** — change more frequently (e.g., account status, user roles), fetched from the service's own database or from events published by Identity Service.

Authorization is on the critical path of every request. Fetching policies or attributes from the database on every decision would add unacceptable latency. Caching is required.

Two caching layers were considered:
- **L1 — In-process cache (Caffeine):** Sub-millisecond access. Lost on service restart. Private to one instance.
- **L2 — Distributed cache (Redis):** Millisecond-range access. Survives restarts. Shared across all instances of a service.

Each service already has its own Redis instance (not shared across services) for domain use. Reusing it for the Policy Agent L2 cache avoids adding new infrastructure.

## Decision
Implement a **two-level cache strategy** in every service's Policy Agent:

- **L1 (Caffeine):** In-process, bounded size, short TTL (configurable per service, default 60s for attributes, longer for policies). Checked first on every decision.
- **L2 (Redis):** Per-service Redis instance, medium TTL (configurable, default 5 minutes for attributes). Checked on L1 miss.
- **Source of truth:** PostgreSQL / Kafka event stream. Fetched on L2 miss and used to populate both caches.

**Cache invalidation:**
- Policy updates: `PolicyUpdatedEvent` from Kafka invalidates both L1 and L2 for the affected policy.
- Attribute changes: specific domain events (`UserStatusChangedEvent`, `AccountStatusChangedEvent`, `KycLevelUpdatedEvent`, `MfaEnabledEvent`) invalidate both levels for the affected subject or resource.
- Access revocation: `AccessRevokedEvent` (see ADR-010) triggers immediate invalidation of both levels, bypassing TTL.

## Consequences

**Benefits:**
- L1 hit eliminates all network calls — authorization adds near-zero latency on cache hit.
- L2 provides warm cache on service restart, preventing cold-start latency spikes.
- Per-service Redis means cache namespacing is natural and there is no cross-service cache pollution.

**Trade-offs:**
- Two invalidation paths must be kept consistent — a bug in one can cause stale data in the other.
- L1 is per-instance: in a horizontally scaled deployment, different instances may briefly hold different cached values after an invalidation event, until all instances consume the Kafka event.
- Redis adds operational complexity per service; each service's Redis must be sized and monitored.
- TTL tuning is non-trivial: too short increases database load, too long increases the eventual consistency window.
