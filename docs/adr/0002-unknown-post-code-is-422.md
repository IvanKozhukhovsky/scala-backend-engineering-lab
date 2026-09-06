# ADR 2: Unknown POST code is 422

## Context

GET /orders/JPY is a missing item at a collection URI. POST /orders with code JPY is a body field on a known URI.

## Decision

We will return 422 unknown_code for a POST body whose code is not in the catalog, and 404 not_found only for GET of a missing item.

## Status

Accepted

## Consequences

Clients can tell a path miss from a domain reject. The OpenAPI document lists both statuses on the matching operations.
