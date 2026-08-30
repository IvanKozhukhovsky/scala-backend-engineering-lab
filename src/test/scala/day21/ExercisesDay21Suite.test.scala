package day21

import munit.FunSuite

class ExercisesDay21Suite extends FunSuite:
  private def oneLine(sql: String): String =
    sql.split("\\s+").filter(_.nonEmpty).mkString(" ").toLowerCase

  test("findFill SQL uses a placeholder and does not embed the code"):
    val sql = findFill("GBP").sql
    val line = oneLine(sql)
    assert(line.contains("select"))
    assert(line.contains("from fill"))
    assert(line.contains("code = ?"))
    assertEquals(sql.count(_ == '?'), 1)
    assert(!sql.contains("GBP"))
    assert(!sql.contains("'?'"))

  test("findFill does not concatenate a hostile code into SQL"):
    val sql = findFill("x'; drop table fill; --").sql
    assert(sql.contains("?"))
    assert(!sql.toLowerCase.contains("drop"))
    assert(!sql.contains("x'"))

  test("insertFill SQL uses three placeholders and does not embed the row"):
    val sql = insertFill(FillRow("EUR", 10, 1080)).sql
    val line = oneLine(sql)
    assert(line.contains("insert into fill"))
    assertEquals(sql.count(_ == '?'), 3)
    assert(!sql.contains("EUR"))
    assert(!sql.contains("1080"))
    assert(!sql.contains("'?'"))
    assert(line.contains("code"))
    assert(line.contains("qty"))
    assert(line.contains("cents"))
