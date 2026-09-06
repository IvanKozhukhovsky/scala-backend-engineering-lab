package day28

import day27.deskHardened

// Independent exercise for scala-028.
// Fill deskObserved and deskRequests yourself. Then write
// packaging/desk/observed.Dockerfile so the file equals
// observedDockerfileOf(deskObserved) exactly (trailing newline included).
//
// deskObserved:
//   hardened: deskHardened (Day 27)
//   probe: 30s / 3s / 3 retries / port 8080 / path /health
//   Reuse quotesProbe if you want — do not alias quotesObserved.
//
// deskRequests:
//   name: desk_http_requests_total
//   help: HTTP requests
//   kind: Counter
//   labels: code=200
//   value: 0
//   Do not alias quotesRequests.
//
// Do not increment inside rateOf.
// Do not HEALTHCHECK /metrics.
// Do not curl localhost (use 127.0.0.1, wget exec-form).
// Do not USER 0 or omit USER.
// Do not bake a token.
// Do not change packaging/desk/Dockerfile or packaging/desk/hardened.Dockerfile
// (those are Day 25 / Day 27 contracts).

/** Desk image with a HEALTHCHECK. Building this does not talk to Docker. */
def deskObserved: ObservedImage =
  ObservedImage(
    hardened = deskHardened,
    probe = HealthProbe(
      interval = "30s",
      timeout = "3s",
      retries = 3,
      port = 8080,
      path = "/health"
    )
  )

/** Desk HTTP counter. Building this does not scrape a process. */
def deskRequests: MetricSample =
  MetricSample(
    name = "desk_http_requests_total",
    help = "HTTP requests",
    kind = MetricKind.Counter,
    labels = List("code" -> "200"),
    value = 0L
  )
