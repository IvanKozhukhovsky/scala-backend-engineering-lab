# ADR 3: Persistence failure is 503

## Context

GET /rates/JPY is a successful query with no row. A failed JDBC session is not a missing item. Day 29's OpenAPI lists 404 for an unknown code and does not list 503.

## Decision

We will map a failed Persistence IO to 503 unavailable. A successful empty Option stays 404 on GET and 422 on POST. GET /health does not transact.

## Status

Accepted

## Consequences

Clients can retry 503 and must not treat a downed catalog as unknown_code. OpenAPI stays silent on 503; RFC 9110 names the status. Retry-After is optional and not required in this unit.
