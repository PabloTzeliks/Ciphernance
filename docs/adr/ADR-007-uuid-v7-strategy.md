# ADR-007: UUID v7 as Primary Key Strategy

## Status
Accepted

## Context
The Ciphernance services require globally unique identifiers for all domain entities (User, Account, Transaction, etc.). The standard Java UUID.randomUUID() generates UUID v4 — random, non-sortable identifiers.

In relational databases (PostgreSQL), random UUID v4 as primary keys causes index fragmentation due to non-sequential inserts on B-tree indexes, leading to page splits and degraded write performance at scale.

Three alternatives were evaluated:
- UUID v4 (Java native) — random, no ordering guarantee, index fragmentation at scale
- UUID v7 (java-uuid-generator library) — timestamp-based, lexicographically sortable, minimal overhead
- Manual UUID v7 implementation — no dependency, but maintenance burden and risk of spec deviation

Java 24 introduces native UUID v7 support via JEP 490, but Ciphernance targets Java 21 LTS.

## Decision
Use UUID v7 generated via the `java-uuid-generator` library (com.fasterxml.uuid:java-uuid-generator:4.3.0).

The generator is called directly inside domain factory methods. A dedicated UuidV7 utility class was intentionally avoided to reduce boilerplate.

When Java 24+ is adopted, migration requires updating each factory method individually rather than a single utility class.

## Consequences

**Gains:**
- Lexicographically sortable identifiers — natural ordering by creation time
- Reduced B-tree index fragmentation on PostgreSQL
- Single dependency, mature library, production-proven

**Costs:**
- External dependency on java-uuid-generator
- Domain layer is coupled to the library — migration to Java 24 native UUID v7 requires touching each factory method
- Team must be aware not to use UUID.randomUUID() directly

## Implementation

```java
// Direct usage inside domain factory methods
// Example: User.create() in identity-service
// Value Objects are constructed by the caller (handler), not inside the factory.
public static User create(Username username, Email email, String passwordHash) {
    Instant now = Instant.now();

    return new User(
        Generators.timeBasedEpochGenerator().generate(),
        username,
        email,
        passwordHash,
        UserRole.USER_ROLE,
        KycLevel.KYC_LEVEL_1,
        false,
        null,
        UserStatus.ACTIVE,
        now,
        now
    );
}

// Caller (RegisterUserHandler) constructs Value Objects before calling the factory:
// Email email = new Email(command.email());
// Username username = new Username(command.username());
// User user = User.create(username, email, passwordEncoder.encode(command.password()));
```

## Migration Path
When Java 24+ is adopted, replace Generators.timeBasedEpochGenerator().generate() with the native Java UUID v7 API in each factory method.
