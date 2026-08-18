package day02

def square(x: Int): Int =
  x * x

def isEven(x: Int): Boolean =
  x % 2 == 0

def applyOperation(value: Int, operation: Int => Int): Int =
  operation(value)

@main
def day02(): Unit =
  val numbers = List(0, 1, 2, 3, 4, 5, 6)

  val doubled = numbers.map(_ * 2)
  val even = numbers.filter(isEven)
  val squared = numbers.map(square)
  val evenSquared =
    numbers
      .filter(isEven)
      .map(square)
  val customResult = applyOperation(10, _ * 3)

  println(s"Original: $numbers")
  println(s"Doubled: $doubled")
  println(s"Even: $even")
  println(s"Squared: $squared")
  println(s"Even squared: $evenSquared")
  println(s"Custom result: $customResult")
