package day21

import munit.FunSuite

class Day21Suite extends FunSuite:
  private def oneLine(sql: String): String =
    sql.split("\\s+").filter(_.nonEmpty).mkString(" ").toLowerCase

  test("findRate SQL uses a placeholder and does not embed the code"):
    val sql = findRate("EUR").sql
    assertEquals(oneLine(sql), "select code, cents from rate where code = ?")
    assert(!sql.contains("EUR"))
    assert(!sql.contains("'?'"))

  test("findRate does not concatenate a hostile code into SQL"):
    val sql = findRate("x'; drop table rate; --").sql
    assert(sql.contains("?"))
    assert(!sql.toLowerCase.contains("drop"))
    assert(!sql.contains("x'"))

  test("insertRate SQL uses two placeholders and does not embed the row"):
    val sql = insertRate(RateRow("EUR", 108)).sql
    assertEquals(
      oneLine(sql),
      "insert into rate (code, cents) values (?, ?)"
    )
    assert(!sql.contains("EUR"))
    assert(!sql.contains("108"))
    assert(!sql.contains("'?'"))

  test("rateOf stays a pure Map lookup"):
    assertEquals(rateOf("EUR"), Some(108))
    assertEquals(rateOf("USD"), None)

  test("rateDdl names the table and the primary key"):
    val ddl = oneLine(rateDdl)
    assert(ddl.contains("create table rate"))
    assert(ddl.contains("primary key"))
    assert(ddl.contains("not null"))
