package day14

import cats.effect.IO

// Independent exercise for scala-014.
// Implement unitPrice, lookupUnitPrice, and lineTotal yourself.
// Do not call unsafeRunSync, Await, Thread.sleep, or println inside those three functions.
// Do not import scala.concurrent.Future.
// Do not put the catalog rules only inside an IO block — unitPrice stays a pure Option
// function. lookupUnitPrice only suspends that lookup. lineTotal uses lookupUnitPrice and map.

val catalog: Map[String, Int] = Map("WIDGET" -> 250, "GADGET" -> 400)

/** Unit price in cents for a known sku, or None. */
def unitPrice(sku: String): Option[Int] =
  catalog.get(sku)

/** Suspend `unitPrice` in IO. Building the value must not run the lookup. */
def lookupUnitPrice(sku: String): IO[Option[Int]] =
  IO(unitPrice(sku))

/** Line total: unit cents times `qty`.
  *
  * Unknown sku: `None`. If `qty` is less than 1, return an IO of `None` even when the sku is known.
  * Use `lookupUnitPrice` and `map`. Do not run the IO.
  */
def lineTotal(sku: String, qty: Int): IO[Option[Int]] =
  if qty < 1 then IO.pure(None)
  else lookupUnitPrice(sku).map(_.map(_ * qty))
