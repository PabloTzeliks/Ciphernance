# ADR-015: Exception Hierarchy — Domain vs Application

## Status
Accepted

## Context
Exceptions needed clear ownership to prevent infrastructure concerns (HTTP status codes, Spring annotations) from leaking into the domain, and to prevent domain invariant violations from being confused with use case orchestration failures.

Without a defined rule, developers tend to put exceptions wherever is convenient — leading to domain classes that import `org.springframework.http.HttpStatus`, or application handlers catching `IllegalArgumentException` from deep inside a value object constructor.

## Decision
Two separate exception hierarchies, one per layer:

### Domain Exceptions (`domain/exception/`)
Represent **invariant violations** — states that should never be reachable if the system is used correctly. Thrown by entities and value objects when their internal rules are broken.

Pure Java. Zero framework dependencies.

Examples:
- `InvalidUserStatusTransitionException` — thrown by `UserStatus` when an illegal state transition is attempted.
- `InvalidAccountStatusTransitionException` — thrown by `AccountStatus`.
- `InvalidKycPromotionException` — thrown by `KycLevel` when a promotion skips a level.
- `InvalidRolePromotionException` / `InvalidRoleRevokeException` — thrown by `User` when role logic is violated.
- `MfaAlreadyEnabledException` / `MfaNotEnabledException` — thrown by `User` when MFA state is inconsistent.
- `InvalidEmailException` / `InvalidUsernameException` / `InvalidPasswordException` — thrown by Value Object constructors on malformed input.

### Application Exceptions (`application/exception/`)
Represent **use case failures** — conditions that are expected during normal system operation, raised by handlers when orchestration constraints are violated.

Three base classes express the category:
- `ResourceNotFoundException` — requested entity does not exist.
- `ResourceAlreadyExistsException` — attempted creation of a duplicate entity.
- `AuthenticationException` — identity or credential verification failed.

Concrete exceptions extend these bases:
- `UserNotFoundException`, `AccountNotFoundException` → `ResourceNotFoundException`
- `DuplicateEmailException`, `DuplicateUsernameException` → `ResourceAlreadyExistsException`
- `InvalidCredentialsException`, `MfaRequiredException`, `InvalidMfaCodeException`, `MfaSetupExpiredException`, `UserInactiveException` → `AuthenticationException`

### HTTP Mapping
HTTP status codes are assigned exclusively in `GlobalExceptionHandler` in the infrastructure layer. No exception in the domain or application layer carries or references an HTTP status code. The mapping rule is:

| Base class | HTTP status |
|---|---|
| `ResourceNotFoundException` | 404 Not Found |
| `ResourceAlreadyExistsException` | 409 Conflict |
| `AuthenticationException` | 401 Unauthorized |
| Domain exceptions (uncaught) | 500 Internal Server Error |

## Consequences

**Gains:**
- Domain exceptions express business language — a reader understands what went wrong from the class name alone.
- Application exceptions express orchestration failures — the caller knows whether to retry, redirect, or report.
- HTTP concerns are isolated to one class in infrastructure — changing a status code requires changing one place.
- Domain layer has zero dependency on any web framework.

**Costs:**
- Two exception packages to navigate. Developers must decide which layer owns a new exception before writing it.
- Domain exceptions that escape to the HTTP boundary unhandled will produce a 500 — requires discipline to ensure all domain exceptions that can reach the web layer are either caught in handlers or mapped in `GlobalExceptionHandler`.
