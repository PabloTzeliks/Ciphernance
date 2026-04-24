# ADR-010: JWT Session Invalidation via Redis Blocklist

## Status
Accepted

## Context
JWT tokens are stateless — once issued, they remain valid until expiration regardless of user status changes. When a user is blocked (fraud, admin action, compliance), their active sessions must be invalidated immediately.

Three approaches were evaluated:
1. Short-lived tokens (5-15 min expiry) — simple, no state, but delayed invalidation
2. Redis blocklist — userId added on block, API Gateway checks on every request
3. Token version — User entity carries tokenVersion, JWT carries version, Gateway validates match

## Decision
Use Redis blocklist (Option B).

When BlockUserHandler publishes AccessRevokedEvent, the API Gateway Policy Agent consumes it immediately (high-priority topic: access-revocation) and adds the userId to a Redis blocklist with TTL matching the token expiry.

Every request through the API Gateway checks the blocklist before forwarding. If the userId is present, the request is rejected with 401 regardless of JWT validity.

## Flow
