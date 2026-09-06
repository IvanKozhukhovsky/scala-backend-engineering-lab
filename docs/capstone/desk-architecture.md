# desk architecture

## Core

Owns: rateOf and orderOf

Does not own: HTTP, JDBC, env, logs, metrics

## Http

Owns: status mapping, JSON, logs, counters

Does not own: catalog lookup, SQL, Docker

## Persistence

Owns: Flyway and ConnectionIO.transact

Does not own: HTTP status codes, rateOf

## Runtime

Owns: Ciris env, HEALTHCHECK, process uid

Does not own: orderOf
