# ADR-010: JWT Session Invalidation via Redis Blocklist

## Status
Accepted

## Context
JWT tokens are stateless — once issued, they remain valid until expiration regardless of user status changes. When a user is blocked or suspended (fraud, admin action, compliance), their active sessions must be invalidated immediately.

Three approaches were evaluated:
1. Short-lived tokens (5-15 min expiry) — simple, no state, but delayed invalidation
2. Redis blocklist — userId added on block, API Gateway checks on every request
3. Token version — User entity carries tokenVersion, JWT carries version, Gateway validates match

## Decision
Use Redis blocklist (Option B).

When BlockUserHandler or SuspendUserHandler publishes AccessRevokedEvent, the API Gateway Policy Agent consumes it immediately (high-priority topic: access-revocation) and adds the userId to a Redis blocklist with TTL matching the token expiry.

Every request through the API Gateway checks the blocklist before forwarding. If the userId is present, the request is rejected with 401 regardless of JWT validity.

## Flow
BlockUserHandler and SuspendUserHandler publishes AccessRevokedEvent to the access-revocation topic (high priority).
API Gateway Policy Agent consumes the event immediately.

Policy Agent adds userId to Redis blocklist with TTL equal to the access token expiry.

On the next request from the blocked user, the API Gateway checks the blocklist.

userId found in blocklist — request rejected with 401 Unauthorized.

TTL expires automatically after the token would have expired anyway — no manual cleanup needed.

## Consequences

**Gains:**
- Immediate session invalidation with no window where blocked users retain access
- Consistent with existing ABAC cache infrastructure — Redis is already present per service
- Decoupled — API Gateway reacts to the event, Identity Service does not poll or notify directly
- TTL automatically cleans up blocklist entries after token expiry — no manual cleanup

**Costs:**
- Redis becomes a critical dependency of the API Gateway security flow
- Redis failure requires fail-closed behavior — availability is sacrificed for security
- Blocklist check adds minor latency to every request (approximately 1ms)
- Blocklist must be replicated if API Gateway runs multiple instances — mitigated by shared Redis