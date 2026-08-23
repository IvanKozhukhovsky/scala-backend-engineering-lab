package day11

class Day11Suite extends munit.FunSuite:
  test("debit 400 from 1000 leaves 600"):
    val wallet = Wallet(1000)
    assertEquals(debit(wallet, 400), Some(Wallet(600)))
    assertEquals(wallet, Wallet(1000))

  test("debit that exceeds the balance is refused"):
    assertEquals(debit(Wallet(1000), 1001), None)

  test("non-positive amounts are refused"):
    assertEquals(debit(Wallet(1000), 0), None)
    assertEquals(debit(Wallet(1000), -1), None)
