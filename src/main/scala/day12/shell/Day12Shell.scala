package day12.shell

import day12.core.Item
import day12.core.priceOf

/** Imperative shell: I/O only. The lookup lives in `priceOf`. */
def printPrice(items: List[Item], sku: String): Unit =
  priceOf(items, sku) match
    case Some(cents) => println(s"$cents")
    case None        => println("unknown")
