package day12

import day12.core.Item
import day12.shell.printPrice

@main
def runDay12(): Unit =
  val catalog = List(Item("SKU-1", 499), Item("SKU-2", 1200))
  printPrice(catalog, "SKU-1")
  printPrice(catalog, "NOPE")
