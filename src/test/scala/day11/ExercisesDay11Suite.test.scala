package day11

class ExercisesDay11Suite extends munit.FunSuite:
  test("SAVE10 takes ten percent and leaves the original invoice"):
    val invoice = Invoice(1000, None, 0)
    assertEquals(applyCoupon(invoice, "SAVE10"), Right(Invoice(1000, Some("SAVE10"), 100)))
    assertEquals(invoice, Invoice(1000, None, 0))

  test("SAVE25 takes twenty-five percent"):
    assertEquals(
      applyCoupon(Invoice(1000, None, 0), "SAVE25"),
      Right(Invoice(1000, Some("SAVE25"), 250))
    )

  test("five cents at ten percent truncates toward zero"):
    assertEquals(
      applyCoupon(Invoice(5, None, 0), "SAVE10"),
      Right(Invoice(5, Some("SAVE10"), 0))
    )

  test("a second coupon is AlreadyApplied even when the new code is valid"):
    val invoiced = Invoice(1000, Some("SAVE10"), 100)
    assertEquals(applyCoupon(invoiced, "SAVE25"), Left(DiscountError.AlreadyApplied))
    assertEquals(applyCoupon(invoiced, "WELCOME"), Left(DiscountError.AlreadyApplied))

  test("an unknown code is UnknownCode with that exact string"):
    assertEquals(
      applyCoupon(Invoice(1000, None, 0), "WELCOME"),
      Left(DiscountError.UnknownCode("WELCOME"))
    )
