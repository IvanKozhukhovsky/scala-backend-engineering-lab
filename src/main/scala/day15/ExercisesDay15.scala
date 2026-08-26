package day15

import cats.effect.IO
import cats.effect.Resource

// Independent exercise for scala-015.
// Implement lease, withLease, and withHeartbeat yourself.
// Do not call unsafeRunSync, Await, Thread.sleep, or println inside those three.
// Do not import scala.concurrent.Future.

final case class Permit(id: String)

/** Resource that acquires a Permit via `open` and always releases via `close`. Use Resource.make.
  */
def lease(
    id: String
)(open: String => IO[Permit], close: Permit => IO[Unit]): Resource[IO, Permit] =
  Resource.make(open(id))(close)

/** Acquire the lease, run `use`, and release — including when `use` fails or is canceled.
  */
def withLease[A](
    id: String
)(open: String => IO[Permit], close: Permit => IO[Unit])(use: Permit => IO[A]): IO[A] =
  lease(id)(open, close).use(use)

/** Run `heartbeat` in the background while `action` runs. Prefer `.background` so the heartbeat is
  * canceled when `action` finishes or is canceled. Do not call `.start` and leave the fiber
  * unmanaged.
  */
def withHeartbeat[A](heartbeat: IO[Nothing], action: IO[A]): IO[A] =
  heartbeat.background.use(_ => action)
