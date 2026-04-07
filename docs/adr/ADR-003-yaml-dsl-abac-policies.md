# ADR-003: YAML DSL for ABAC policy definitions

## Status
Accepted

## Context
ABAC policies define the rules that govern access: who can do what, under which conditions, on which resources. These rules must be:
- Human-readable so they can be reviewed, audited, and debated without requiring Java expertise.
- Version-controlled alongside the codebase so policy changes are traceable through git history.
- Structured enough to be compiled into executable policy objects by Identity Service.

Options considered:
1. **Java code:** Policies written directly as Spring Security beans or programmatic rules. Simple to implement, but not auditable by non-developers and requires redeployment for any policy change.
2. **Rego (OPA):** Powerful, purpose-built policy language. Adds an OPA dependency and a learning curve; couples policies to an external engine.
3. **YAML DSL:** Custom lightweight DSL expressed in YAML. Human-readable, version-controllable, no external dependency, compiled by Identity Service into internal policy objects.

## Decision
Define policies as **YAML files** in the `/policies` directory at the root of the monorepo. The DSL supports:
- `policy-id`: unique identifier
- `version`: semver for tracking
- `target`: which resource type and action this policy governs
- `rules`: list of conditions combining subject attributes, resource attributes, and environment attributes
- `effect`: `PERMIT` or `DENY`
- `obligations`: optional post-decision side-effects (e.g., require audit log)

Identity Service reads and compiles these files at startup. On compilation, it publishes a `PolicyUpdatedEvent` to Kafka for each changed policy. Services with local Policy Agents consume this event and replace their in-memory policy store.

Policy files are organized by domain: `transfer-policies.yml`, `account-policies.yml`, `fraud-policies.yml`.

## Consequences

**Benefits:**
- Policies are readable by security reviewers, compliance teams, and architects without needing to read Java.
- All policy changes are tracked in git with author, date, and commit message.
- No runtime dependency on an external policy engine.
- DSL can evolve incrementally as new condition types are needed.

**Trade-offs:**
- The DSL is custom and must be maintained within the project — it lacks the expressiveness and community support of Rego.
- Identity Service must implement and maintain the YAML compiler; bugs in the compiler affect all downstream policy evaluations.
- Adding new condition types requires updating the compiler and potentially all Policy Agent implementations.
