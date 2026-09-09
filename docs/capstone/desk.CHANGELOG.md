# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.1.0] - 2026-09-09

### Added

- HTTP surface from OpenAPI 3.1.2 (`GET /health`, `GET /orders/{code}`, `POST /orders`).
- Persistence lookup with 503 on a failed session (ADR 3).
- Observed runtime image: non-root `USER 1000` and `HEALTHCHECK` on loopback `/health`.

### Security

- Action SHA pins in the release workflow twin; secrets stay out of image layers.

This repository remains a learning lab. desk 0.1.0 is initial development, not a hosted production service.

[unreleased]: https://github.com/IvanKozhukhovsky/scala-backend-engineering-lab/compare/desk-v0.1.0...HEAD
[0.1.0]: https://github.com/IvanKozhukhovsky/scala-backend-engineering-lab/releases/tag/desk-v0.1.0
