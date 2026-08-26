package day15

import cats.effect.IO
import cats.effect.IOApp
import cats.effect.Resource

final case class Conn(id: String)

/** Open a named connection. Building this IO does not open anything yet. */
def open(id: String): IO[Conn] =
  IO(Conn(id))

/** Close a connection. */
def close(c: Conn): IO[Unit] =
  IO.unit

/** Pair open with close. Release runs when use succeeds, fails, or is canceled.
  */
def connection(id: String): Resource[IO, Conn] =
  Resource.make(open(id))(close)

/** Run a query while the connection is held. */
def query(c: Conn): IO[String] =
  IO.pure(s"rows:${c.id}")

/** Same lifecycle, but open/close are injected so tests can count. Prefer this shape when the
  * acquire/release effects need to be observed.
  */
def connectionWith(
    id: String,
    acquire: String => IO[Conn],
    release: Conn => IO[Unit]
): Resource[IO, Conn] =
  Resource.make(acquire(id))(release)

/** Keep `pulse` running in the background while `action` runs. Leaving the resource scope cancels
  * the pulse fiber.
  */
def whilePulsing[A](pulse: IO[Nothing], action: IO[A]): IO[A] =
  pulse.background.use(_ => action)

object Day15 extends IOApp.Simple:
  val run: IO[Unit] =
    connection("orders").use { c =>
      query(c).flatMap(IO.println)
    }
