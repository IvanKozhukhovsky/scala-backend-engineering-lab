package day12.shell

import day12.core.ReserveError
import day12.core.Stock
import day12.core.reserve

/** Imperative shell: I/O only. Do not put reserve rules here. */
def printReserve(stock: Stock, sku: String, qty: Int): Unit =
  reserve(stock, sku, qty) match
    case Right(next)                     => println(s"onHand ${next.onHand}")
    case Left(ReserveError.WrongSku(s))  => println(s"wrong sku: $s")
    case Left(ReserveError.NotEnough(n)) => println(s"not enough: $n")

@main
def exercisesDay12(): Unit =
  println("Implement reserve in day12.core, then run the suite and /review-exercise.")
