package day10

/** Restrict `value` to the closed range `[lo, hi]`. Caller must pass `lo <= hi`. */
def clamp(value: Int, lo: Int, hi: Int): Int =
  if value < lo then lo
  else if value > hi then hi
  else value

@main
def day10(): Unit =
  println(s"150 clamped to 1..100: ${clamp(150, 1, 100)}")
  println(s"0 clamped to 1..100: ${clamp(0, 1, 100)}")
  println(s"50 clamped to 1..100: ${clamp(50, 1, 100)}")
