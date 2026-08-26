package day15

import cats.effect.IO
import cats.effect.unsafe.implicits.global

import scala.concurrent.duration.*

class ExercisesDay15Suite extends munit.FunSuite:
  test("withLease opens and closes once on success"):
    var opened = 0
    var closed = 0
    val open = (id: String) =>
      IO {
        opened += 1
        Permit(id)
      }
    val close = (_: Permit) =>
      IO {
        closed += 1
      }
    val got = withLease("seat-1")(open, close)(p => IO.pure(p.id)).unsafeRunSync()
    assertEquals(got, "seat-1")
    assertEquals(opened, 1)
    assertEquals(closed, 1)

  test("withLease still closes when use fails"):
    var closed = 0
    val open = (id: String) => IO(Permit(id))
    val close = (_: Permit) => IO { closed += 1 }
    val boom = withLease("seat-1")(open, close)(_ => IO.raiseError(new RuntimeException("boom")))
    intercept[RuntimeException](boom.unsafeRunSync())
    assertEquals(closed, 1)

  test("withLease still closes when canceled"):
    var closed = 0
    val open = (id: String) => IO(Permit(id))
    val close = (_: Permit) => IO { closed += 1 }
    val program = withLease("seat-1")(open, close)(_ => IO.sleep(5.seconds))
    val canceled =
      for
        fiber <- program.start
        _ <- IO.sleep(50.millis)
        _ <- fiber.cancel
      yield ()
    canceled.unsafeRunSync()
    assertEquals(closed, 1)

  test("withHeartbeat cancels the heartbeat when action finishes"):
    var ticks = 0
    val pulse = (IO { ticks += 1 } >> IO.sleep(20.millis)).foreverM
    withHeartbeat(pulse, IO.sleep(80.millis)).unsafeRunSync()
    val afterStop = ticks
    Thread.sleep(80)
    assertEquals(ticks, afterStop)
    assert(afterStop >= 1)

  test("lease can be composed before use"):
    var closed = 0
    val open = (id: String) => IO(Permit(id))
    val close = (_: Permit) => IO { closed += 1 }
    val got = lease("seat-2")(open, close).use(p => IO.pure(p.id)).unsafeRunSync()
    assertEquals(got, "seat-2")
    assertEquals(closed, 1)
