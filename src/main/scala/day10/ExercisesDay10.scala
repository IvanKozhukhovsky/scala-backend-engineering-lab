package day10

// Independent exercise for scala-010: do not change this file.
// The skill is writing properties in ExercisesDay10Suite.test.scala.

/** Zero-based index of the first item on a 1-based page.
  *
  * Defined when page >= 1 and pageSize >= 1.
  */
def pageOffset(page: Int, pageSize: Int): Int =
  (page - 1) * pageSize

@main
def exercisesDay10(): Unit =
  println(
    "Do not change this file. Write properties in src/test/scala/day10/ExercisesDay10Suite.test.scala"
  )
