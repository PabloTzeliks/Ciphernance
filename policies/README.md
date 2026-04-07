# ABAC Policies

This directory contains the YAML DSL policy definitions for Ciphernance's Attribute-Based Access Control system.

## How It Works

1. **Authoring:** Policies are written as YAML files in this directory and versioned in git. Every policy change is traceable through git history.

2. **Compilation:** At startup, **Identity Service** reads all YAML files in this directory, validates their structure, and compiles them into internal `CompiledPolicy` objects held in memory.

3. **Distribution:** After compilation, Identity Service publishes a `PolicyUpdatedEvent` to the `policy-updates` Kafka topic for each policy. The event carries the compiled policy payload and its version.

4. **Local Policy Agents:** Each service (api-gateway, account-service, transaction-service, fraud-service, audit-service) runs an embedded Policy Agent that consumes `policy-updates` events and replaces its in-memory policy store. This makes policies **eventually consistent** across the system.

5. **Full Sync:** On startup, each service subscribes to the `full-sync` topic to receive a complete snapshot of all current policies, avoiding missed updates from before the service started.

6. **Revocation:** Access revocation bypasses normal distribution — see `access-revocation` Kafka topic (ADR-006).

## DSL Structure

```yaml
policy-id: string          # Unique identifier (kebab-case)
version: string            # Semver (e.g., "1.0.0")
description: string        # Human-readable summary
target:
  resource-type: string    # Domain resource this policy governs
  action: string           # Action being authorized (e.g., INITIATE, READ, BLOCK)
rules:
  - id: string             # Rule identifier
    description: string
    conditions:
      subject:             # Attributes of the requesting principal
        - attribute: string
          operator: string # EQ, NEQ, IN, HAS_ROLE, GT, LT
          value: any
      resource:            # Attributes of the target resource
        - attribute: string
          operator: string
          value: any
      environment:         # Contextual attributes (time, IP, etc.)
        - attribute: string
          operator: string
          value: any
    effect: PERMIT | DENY
    obligations:           # Optional side-effects on decision
      - type: string       # e.g., AUDIT_LOG, NOTIFY
        params: object
```

## Files

| File | Domain |
|------|--------|
| `transfer-policies.yml` | Rules governing who can initiate and approve transfers |
| `account-policies.yml` | Rules governing account access, blocking, and attribute reads |
| `fraud-policies.yml` | Rules governing fraud analysis access and verdict publishing |
