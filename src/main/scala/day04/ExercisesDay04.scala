package day04

case class Product(name: String, price: Double, category: String)

def findGoalProductNames(products: List[Product]): List[String] =
  products
    .filter(_.category == "electronics")
    .filter(_.price >= 1000)
    .map(_.name)

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

def parseCoordinates(coordinates: List[(Int, Int)]): List[String] =
  coordinates.map { case (x, y) =>
    s"x = $x, y = $y"
  }

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

  println(s"Electronics above 100: ${findGoalProductNames(products)}")

  println(s"Result: ${describeProduct(Product("Laptop", 1200, "electronics"))}")

  val coordinates =
    List(
      (0, 0),
      (10, 5),
      (-2, 4),
      (3, -7)
    )
  println(s"Coordinates: ${parseCoordinates(coordinates)}")
