package day04

case class Product(name: String, price: Double, category: String)

def describeProduct(product: Product): String =
  product match
    case Product(_, price, "electronics") if price >= 1000 =>
      "premium electronics"
    case Product(_, _, "electronics") =>
      "electronics"
    case Product(_, _, "books") =>
      "book"
    case _ =>
      "other"

@main
def exercisesDay04(): Unit =
  val products =
    List(
      Product("Laptop", 1200.0, "electronics"),
      Product("Mouse", 25.0, "electronics"),
      Product("Book", 15.0, "books"),
      Product("Monitor", 350.0, "electronics"),
      Product("Scala Guide", 40.0, "books")
    )

  val goalProduct =
    products
      .filter(_.category == "electronics")
      .filter(_.price > 100)
      .map(_.name)
  println(s"Electronics above 100: $goalProduct")

  val result = describeProduct(Product("Laptop", 1200, "electronics"))
  println(s"Result: $result")

  val coordinates =
    List(
      (0, 0),
      (10, 5),
      (-2, 4),
      (3, -7)
    )

  val parsedCoordinates =
    coordinates.map { case (x, y) =>
      s"x = $x, y = $y"
    }
  println(s"Coordinates: $parsedCoordinates")
