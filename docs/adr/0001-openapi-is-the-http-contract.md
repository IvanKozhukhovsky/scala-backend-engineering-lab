# ADR 1: OpenAPI is the HTTP contract

## Context

Days 17 through 19 encoded the HTTP surface as HttpRoutes. scala-030 will implement the capstone with TDD. A reviewer who only reads Scala cannot see the intended operations without running tests.

## Decision

We will keep a checked-in OpenAPI 3.1.2 document as the HTTP contract. Tests compare the file to openapiOf. Implementation must match the spec.

## Status

Accepted

## Consequences

TDD has an oracle without Ember. Methods omitted from a path are not documented operations; the service still answers 405. Spec drift fails a test. This unit does not generate http4s from OpenAPI.
