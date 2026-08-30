package day20

import cats.effect.IO
import cats.effect.Resource
import org.http4s.HttpApp
import org.http4s.Uri
import com.comcast.ip4s.ipv4
import com.comcast.ip4s.port
import org.http4s.client.Client
import org.http4s.server.Server
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.ember.client.EmberClientBuilder

// Independent exercise for scala-020.
// Implement orderServe and withOrderLive yourself.
// Bind 127.0.0.1 and port 0. Pass server.baseUri into the client.
// Do not use Client.fromHttpApp — that still skips the socket.
// Do not call unsafeRunSync, Await, or println inside those two.
// Consume a Response body inside Client.run(...).use, not after the Resource exits.
// Keep convertAmount / rateOf pure — this file is the Ember harness, not a new catalog.

/** Bind loopback on an ephemeral port. Building the Resource does not listen yet. */
def orderServe(app: HttpApp[IO]): Resource[IO, Server] =
  EmberServerBuilder
    .default[IO]
    .withHost(ipv4"127.0.0.1")
    .withPort(port"0")
    .withHttpApp(app)
    .build

/** Acquire Ember server + client, run `run` with the live base URI, release both. */
def withOrderLive[A](app: HttpApp[IO])(run: (Uri, Client[IO]) => IO[A]): IO[A] =
  Resource
    .both(orderServe(app), EmberClientBuilder.default[IO].build)
    .use { case (server, client) =>
      run(server.baseUri, client)
    }
