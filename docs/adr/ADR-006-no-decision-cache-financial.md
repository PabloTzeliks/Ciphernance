# ADR-006: No decision-level cache in financial authorization

## Status
Accepted

## Context
A common optimization in authorization systems is to cache the **decision itself** — the outcome of evaluating a policy against a set of attributes — keyed by (subject, action, resource). This avoids re-evaluating policy logic on repeated identical requests and can significantly reduce CPU usage under high load.

However, in financial systems, the correctness of each authorization decision is critical. A cached `PERMIT` decision could allow a transaction that should be denied because:
- The user's account was suspended after the last decision.
- A fraud event triggered access revocation.
- A policy was updated to restrict the action.

The window between when a decision is cached and when the invalidating event is processed could be milliseconds or seconds — but in a financial system, even a brief window of incorrect authorization is a compliance and security risk.

## Decision
**No decision-level cache.** Every authorization request triggers a full policy evaluation by the local PDP (PolicyEngine). The evaluation uses attributes and policies from L1/L2 cache (see ADR-005), but the act of combining them into a decision is never cached.

This means: given the same (subject, action, resource) tuple, the PDP will re-evaluate every time. If the underlying attributes or policies have changed, the new decision will reflect that immediately — bounded only by the attribute/policy cache TTL, not by a decision cache TTL.

Access revocation (`AccessRevokedEvent`) invalidates attribute and policy caches immediately, ensuring that the next re-evaluation uses the updated state.

## Consequences

**Benefits:**
- Eliminates the risk of stale `PERMIT` decisions persisting after a revocation or policy change.
- Authorization correctness is bounded only by the attribute/policy cache TTL (already short) — not by an additional decision cache layer.
- Simpler implementation: no decision cache key design, no decision cache invalidation logic.
- Compliance posture: every decision is freshly computed, which is defensible in audits.

**Trade-offs:**
- Higher CPU usage per request compared to a system with decision caching — policy evaluation runs on every authorization call.
- Under extreme load, policy evaluation becomes a CPU bottleneck. Mitigation: Policy Agent is in-process (no network calls on L1 hit), and Caffeine L1 keeps attribute lookups cheap.
- This decision must be revisited if performance profiling shows policy evaluation is a measurable bottleneck at production-scale load.
