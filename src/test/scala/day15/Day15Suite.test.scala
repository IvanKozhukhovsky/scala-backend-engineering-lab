package day15

import cats.effect.IO
import cats.effect.unsafe.implicits.global

import scala.concurrent.duration.*

class Day15Suite extends munit.FunSuite:
  test("connection use opens and closes once"):
    var opened = 0
    var closed = 0
    val acquire = (id: String) =>
      IO {
        opened += 1
        Conn(id)
      }
    val release = (_: Conn) =>
      IO {
        closed += 1
      }
    val got = connectionWith("orders", acquire, release).use(query).unsafeRunSync()
    assertEquals(got, "rows:orders")
    assertEquals(opened, 1)
    assertEquals(closed, 1)

  test("release still runs when use fails"):
    var closed = 0
    val acquire = (id: String) => IO(Conn(id))
    val release = (_: Conn) => IO { closed += 1 }
    val boom = connectionWith("orders", acquire, release).use(_ =>
      IO.raiseError(new RuntimeException("boom"))
    )
    intercept[RuntimeException](boom.unsafeRunSync())
    assertEquals(closed, 1)

  test("release still runs when use is canceled"):
    var closed = 0
    val acquire = (id: String) => IO(Conn(id))
    val release = (_: Conn) => IO { closed += 1 }
    val program =
      connectionWith("orders", acquire, release).use(_ => IO.sleep(5.seconds))
    val canceled =
      for
        fiber <- program.start
        _ <- IO.sleep(50.millis)
        _ <- fiber.cancel
      yield ()
    canceled.unsafeRunSync()
    assertEquals(closed, 1)

  test("whilePulsing cancels the pulse when action finishes"):
    var ticks = 0
    val pulse = IO { ticks += 1 } >> IO.sleep(20.millis)
    val looping = pulse.foreverM
    whilePulsing(looping, IO.sleep(80.millis)).unsafeRunSync()
    val afterStop = ticks
    Thread.sleep(80)
    assertEquals(ticks, afterStop)
    assert(afterStop >= 1)

  test("query returns rows for the connection id"):
    assertEquals(connection("orders").use(query).unsafeRunSync(), "rows:orders")
