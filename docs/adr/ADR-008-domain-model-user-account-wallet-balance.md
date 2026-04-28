# ADR-008: Domain Model — User, Account, Wallet, Balance

## Status
Accepted

## Context
Ciphernance is a payment engine, not a traditional bank. The domain model must support multiple assets (BRL, ZEC, USD) and multiple contexts per user (personal, business) without requiring a full rewrite.

Initial modeling used Account with AccountNumber in a traditional banking style. This was too rigid for a payment engine that may serve brokers, agentic payment systems, and multi-asset scenarios.

## Decision
Adopt a four-layer domain model:

User → Account(s) → Wallet(s) → Balance

- User: authentication and authorization identity (Identity Service)
- Account: identity anchor in the payment engine. A User may have multiple Accounts (personal, business). Owned by Identity Service.
- Wallet: denominated in a single Asset. UNIQUE(accountId, asset) — one wallet per asset per Account. Asset is immutable after creation. Owned by Wallet Service.
- Balance: financial state of a Wallet — available, reserved, pending. Owned by Wallet Service.

AccountNumber as a banking concept was removed. The UUID is the technical identifier. Human-readable references are deferred to future.

OwnerType (USER, AGENT, MERCHANT) is deferred — MVP only supports USER.

## MVP Constraints
- One User has one Account
- One Account has one Wallet (BRL)
- Account created synchronously during User registration by Identity Service
- Wallet created asynchronously by Wallet Service upon consuming `AccountCreatedEvent` — not in the registration handler
- Single asset: BRL

## Implementation Notes

**`Account.ownerId` field naming:** The Account entity references its owner via `ownerId` (not `userId`). This naming is intentional — it remains forward-compatible with the deferred `OwnerType` (USER, AGENT, MERCHANT). In MVP only USER is supported, but renaming the field later would require a schema migration and API change. All port methods (`findByOwnerId`, `existsByOwnerId`) follow the same convention.

## Consequences

**Gains:**
- Extensible to multi-asset without structural refactoring
- Supports multiple Accounts per User (personal/business contexts) naturally
- Clean separation between identity (Account) and financial state (Wallet/Balance)
- Aligned with how payment processors (Stripe, Adyen) model accounts

**Costs:**
- More layers than a simple bank account model
- Wallet Service must listen to AccountCreatedEvent to create its own projection
- Balance as separate concept adds complexity vs a single balance field
