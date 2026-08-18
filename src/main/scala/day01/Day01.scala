package day01

def classifyTemperature(temperature: Double): String =
  if temperature < 0 then "freezing"
  else if temperature < 20 then "cool"
  else "warm"

def multiply(value: Int, factor: Int): Int =
  value * factor

@main
def day01(): Unit =
  val language = "Scala"
  val version: Int = 3

  var attempts = 0
  attempts += 1

  val x = 10
  val y = 20

  val maximum =
    if x > y then x
    else y

  val message =
    val sum = x + y
    s"$language $version: sum=$sum, maximum=$maximum, attempts=$attempts"

  val doubled = multiply(x, 2)

  println(message)
  println(s"Doubled x: $doubled")
  println(s"Temperature: ${classifyTemperature(18.5)}")

  val result =
    if x > 10 then 100
    else "small"

  println(result)
