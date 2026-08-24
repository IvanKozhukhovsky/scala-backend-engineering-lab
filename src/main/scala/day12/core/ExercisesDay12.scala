package day12.core

// Independent exercise for scala-012.
// Implement reserve yourself.
// Do not import day12.shell.
// Do not call println, System.currentTimeMillis, or mutate a var inside reserve.
// Do not move the decision into printReserve — that function is the shell.

case class Stock(sku: String, onHand: Int)

enum ReserveError:
  case WrongSku(found: String)
  case NotEnough(onHand: Int)

/** Reduce `onHand` when `sku` matches and `qty` is between 1 and `onHand` inclusive.
  *
  * If `sku` does not match `stock.sku`, return `WrongSku` with the stock's sku and do not look at
  * `qty`. If `qty` is less than 1 or greater than `onHand`, return `NotEnough` with the current
  * `onHand`. Success: a new `Stock`; do not mutate the input.
  */
def reserve(stock: Stock, sku: String, qty: Int): Either[ReserveError, Stock] =
  if stock.sku != sku then Left(ReserveError.WrongSku(stock.sku))
  else if qty < 1 || qty > stock.onHand then Left(ReserveError.NotEnough(stock.onHand))
  else Right(stock.copy(onHand = stock.onHand - qty))
