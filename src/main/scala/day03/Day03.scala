package day03

@main
def day03(): Unit =
  val numbers = List(1, 2, 3, 4, 5, 6)

  val doubled = numbers.map(_ * 2)
  val even = numbers.filter(_ % 2 == 0)
  val firstGreaterThanThree = numbers.find(_ > 3)
  val hasNegative = numbers.exists(_ < 0)
  val allPositive = numbers.forall(_ > 0)
  val nested = numbers.map(x => List(x, x * 10))
  val flattened = nested.flatten
  val flatMapped = numbers.flatMap(x => List(x, x * 10))
  val evenMultiplied =
    numbers.collect:
      case x if x % 2 == 0 => x * 10
  val sum = numbers.foldLeft(0)((acc, value) => acc + value)

  println(s"Original: $numbers")
  println(s"Doubled: $doubled")
  println(s"Even: $even")
  println(s"Found: $firstGreaterThanThree")
  println(s"Has negative: $hasNegative")
  println(s"All positive: $allPositive")
  println(s"Nested: $nested")
  println(s"Flattened: $flattened")
  println(s"FlatMapped: $flatMapped")
  println(s"Collected: $evenMultiplied")
  println(s"Sum: $sum")
