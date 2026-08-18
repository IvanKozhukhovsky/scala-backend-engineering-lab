package day02

def isWarm(temperature: Int): Boolean =
  temperature >= 20

// F = C × 9 / 5 + 32
def toFahrenheit(celsius: Int): Double =
  celsius * 9.0 / 5.0 + 32.0

@main
def exercisesDay02(): Unit =
  val temperatures = List(-5, 0, 7, 18, 21, 25, 32, 40)
  println(s"$temperatures °C")

  val result =
    temperatures
      .filter(isWarm)
      .map(toFahrenheit)
  println(s"$result °F")

  val describeTemperature: Int => String =
    temperature =>
      if temperature < 0 then "freezing"
      else if temperature < 20 then "cold"
      else if temperature < 30 then "warm"
      else "hot"

  val descriptions = temperatures.map(describeTemperature)
  println(s"$descriptions")
