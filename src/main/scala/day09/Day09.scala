package day09

/** Whole-percent discount on an amount in cents. Truncates toward zero. */
def discounted(amountCents: Int, percent: Int): Int =
  amountCents * (100 - percent) / 100

@main
def day09(): Unit =
  println(s"900: ${discounted(1000, 10)}")
  println(s"250: ${discounted(250, 0)}")
  println(s"2: ${discounted(5, 50)}")
