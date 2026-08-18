package day01

/** Exercise:
  *   - define `name`, `age`, `temperature`, and `isStudent` with suitable types;
  *   - classify age as minor/adult/senior;
  *   - classify temperature as freezing/cold/warm/hot;
  *   - build and print one interpolated summary string.
  */
def classifyAgeCategory(age: Int): String =
  if age < 18 then "a minor"
  else if age < 65 then "an adult"
  else "a senior"

def classifyTemperatureCategory(temperature: Int): String =
  if temperature < 0 then "freezing"
  else if temperature < 20 then "cold"
  else if temperature < 30 then "warm"
  else "hot"

@main
def exercisesDay01(): Unit =
  val name = "Alice"
  val age = 17
  val temperature = -2
  val isStudent = false

  val message =
    s"$name is ${classifyAgeCategory(age)}. Temperature is ${classifyTemperatureCategory(temperature)}. Student: $isStudent"

  println(message)
