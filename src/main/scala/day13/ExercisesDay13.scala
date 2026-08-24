package day13

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

// Independent exercise for scala-013.
// Implement unitPrice, fetchUnitPrice, and fetchLineTotal yourself.
// Do not call Await, Thread.sleep, or println inside those three functions.
// Do not import cats.effect.
// Do not put the catalog rules only inside a Future block — unitPrice stays a pure Option
// function. fetchUnitPrice only wraps that lookup. fetchLineTotal uses fetchUnitPrice and map.

val catalog: Map[String, Int] = Map("WIDGET" -> 250, "GADGET" -> 400)

/** Unit price in cents for a known sku, or None. */
def unitPrice(sku: String): Option[Int] =
  if sku == "WIDGET" || sku == "GADGET" then Some(catalog(sku))
  else None

/** Run `unitPrice` on the given ExecutionContext. Only wrap the lookup. */
def fetchUnitPrice(sku: String)(using ExecutionContext): Future[Option[Int]] =
  Future {
    unitPrice(sku)
  }

/** Eventual line total: unit cents times `qty`.
  *
  * Unknown sku: `None`. If `qty` is less than 1, return a Future of `None` even when the sku is
  * known. Use `fetchUnitPrice` and `map`. Do not Await.
  */
def fetchLineTotal(sku: String, qty: Int)(using ExecutionContext): Future[Option[Int]] =
  // 1st solution
  // fetchUnitPrice(sku)
  //   .map(maybePrice => maybePrice.filter(_ => qty >= 1).map(_ * qty))

  // 2nd solution
  if qty < 1 then Future.successful(None)
  else fetchUnitPrice(sku).map(maybePrice => maybePrice.map(_ * qty))

@main
def exercisesDay13(): Unit =
  println(
    "Implement unitPrice, fetchUnitPrice, and fetchLineTotal, then run the suite and /review-exercise."
  )
