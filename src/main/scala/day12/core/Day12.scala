package day12.core

case class Item(sku: String, priceCents: Int)

/** Price in cents for the first item whose sku matches, or None. */
def priceOf(items: List[Item], sku: String): Option[Int] =
  items.find(_.sku == sku).map(_.priceCents)
