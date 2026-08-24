package day12.core

class ExercisesDay12Suite extends munit.FunSuite:
  test("reserve 3 from 10 leaves 7 and the original stock"):
    val stock = Stock("SKU-1", 10)
    assertEquals(reserve(stock, "SKU-1", 3), Right(Stock("SKU-1", 7)))
    assertEquals(stock, Stock("SKU-1", 10))

  test("exact quantity leaves zero on hand"):
    assertEquals(reserve(Stock("SKU-1", 10), "SKU-1", 10), Right(Stock("SKU-1", 0)))

  test("wrong sku is WrongSku with the stock's sku even when qty is invalid"):
    val stock = Stock("SKU-1", 10)
    assertEquals(reserve(stock, "SKU-2", 3), Left(ReserveError.WrongSku("SKU-1")))
    assertEquals(reserve(stock, "SKU-2", 0), Left(ReserveError.WrongSku("SKU-1")))

  test("qty above onHand is NotEnough with the current onHand"):
    assertEquals(reserve(Stock("SKU-1", 10), "SKU-1", 11), Left(ReserveError.NotEnough(10)))

  test("non-positive qty is NotEnough with the current onHand"):
    assertEquals(reserve(Stock("SKU-1", 10), "SKU-1", 0), Left(ReserveError.NotEnough(10)))
    assertEquals(reserve(Stock("SKU-1", 10), "SKU-1", -1), Left(ReserveError.NotEnough(10)))
