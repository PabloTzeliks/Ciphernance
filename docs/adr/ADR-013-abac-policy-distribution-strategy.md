# ADR-013: ABAC Policy Distribution Strategy

## Status
Accepted

## Context
The initial ABAC design (ADR-002, ADR-003) described Identity Service compiling YAML policies at startup and distributing them to all services via Kafka (`policy-updates` topic). Each service's Policy Agent would maintain an in-memory policy store kept fresh by consuming these events.

This model introduces a critical startup dependency: if Identity Service is unavailable, no service can load its policies and therefore cannot evaluate any authorization decision. This contradicts the Choreography Saga principle of service autonomy (ADR-001) and the goal of removing single points of failure from the authorization hot path (ADR-002).

Two distribution models were evaluated:

1. **Kafka-distributed policies:** Identity Service is the single source of truth; policies flow via events at runtime.
2. **Embedded policies:** Each service owns its YAML policy files in its own resources; loads them independently at startup.

## Decision
Policies are **embedded in each service** as YAML files under the service's own `resources/policies/` directory. Each service loads and compiles its own policies at startup independently, with no dependency on Identity Service or Kafka.

Identity Service distributes only **dynamic user and account attributes** via Kafka — not policy rules:
- `UserStatusChangedEvent`
- `AccountStatusChangedEvent`
- `KycLevelUpdatedEvent`
- `MfaEnabledEvent`
- `AccessRevokedEvent` (high-priority topic)
- `FullSyncEvent` (startup warm-up, see ADR-002)

This creates a clear separation between two categories:

| Category | Owner | Change mechanism |
|---|---|---|
| Policy rules (static) | Each service (embedded YAML) | Redeploy of that service |
| User/account attributes (dynamic) | Identity Service (Kafka events) | Runtime, eventually consistent |

The `policy-updates` Kafka topic described in ADR-002 and ADR-003 is **not implemented**. Policy changes require a service redeploy.

## Consequences

**Gains:**
- Zero startup coupling between services and Identity Service — each service boots independently regardless of IAM availability.
- Each service is truly autonomous in its authorization path.
- Identity Service failure does not affect policy evaluation in other services.
- Simpler Policy Agent implementation — no Kafka consumer needed for policy loading.

**Costs:**
- Policy rule changes require a redeploy of every affected service, not a single Kafka publish.
- No central policy management UI in MVP — policy review requires reading YAML files in each service's repository.
- Future migration to centralized policy management (OPA, AWS AppConfig) will require extracting policies from each service JAR.

## Future
If centralized policy management without redeploy coupling is needed, OPA or AWS AppConfig are natural candidates. The embedded YAML DSL (ADR-003) is designed to be portable — migrating means moving the YAML files and pointing the compiler at the new source, not redesigning the DSL.
