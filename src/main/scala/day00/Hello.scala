package day00

def multiply(value: Int, factor: Int): Int =
  value * factor

@main
def hello(): Unit =
  val numbers = List(1, 2, 3, 4, 5)

  val doubled = numbers.map(_ * 2)
  println(doubled)

  val result = numbers.map(number => multiply(number, 2))
  println(result)
