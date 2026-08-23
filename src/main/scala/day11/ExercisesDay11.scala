package day11

// Independent exercise for scala-011.
// Implement applyCoupon yourself.
// Do not call println, System.currentTimeMillis, or mutate a var inside applyCoupon.
// Do not move the decision into printApply — that function is the shell.

case class Invoice(subtotalCents: Int, appliedCode: Option[String], discountCents: Int)

enum DiscountError:
  case AlreadyApplied
  case UnknownCode(code: String)

/** Attach a coupon once. Valid codes: SAVE10 (10%) and SAVE25 (25%).
  *
  * If `appliedCode` is already `Some`, return `AlreadyApplied` without looking at `code`. Unknown
  * code: `UnknownCode` with that exact string. Discount is `subtotalCents * percent / 100`,
  * truncated toward zero (same idea as Day 09). Return a new invoice; do not mutate the input.
  */
def applyCoupon(invoice: Invoice, code: String): Either[DiscountError, Invoice] =
  invoice.appliedCode match
    case Some(_) => Left(DiscountError.AlreadyApplied)
    case None    =>
      if code == "SAVE10" then
        val sale = invoice.subtotalCents * 10 / 100
        Right(invoice.copy(invoice.subtotalCents, Some(code), sale))
      else if code == "SAVE25" then
        val sale = invoice.subtotalCents * 25 / 100
        Right(invoice.copy(invoice.subtotalCents, Some(code), sale))
      else Left(DiscountError.UnknownCode(code))

/** Imperative shell: I/O only. Do not put coupon rules here. */
def printApply(invoice: Invoice, code: String): Unit =
  applyCoupon(invoice, code) match
    case Right(next)                        => println(s"discount ${next.discountCents}")
    case Left(DiscountError.AlreadyApplied) => println("already applied")
    case Left(DiscountError.UnknownCode(c)) => println(s"unknown: $c")

@main
def exercisesDay11(): Unit =
  println("Implement applyCoupon, then run the suite and /review-exercise.")
