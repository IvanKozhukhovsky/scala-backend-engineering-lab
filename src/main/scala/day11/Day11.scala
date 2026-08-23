package day11

case class Wallet(balanceCents: Int)

/** Subtract `amountCents` when it is positive and does not exceed the balance. */
def debit(wallet: Wallet, amountCents: Int): Option[Wallet] =
  if amountCents <= 0 || amountCents > wallet.balanceCents then None
  else Some(wallet.copy(balanceCents = wallet.balanceCents - amountCents))

/** Imperative shell: I/O only. The decision lives in `debit`. */
def printDebit(wallet: Wallet, amountCents: Int): Unit =
  debit(wallet, amountCents) match
    case Some(next) => println(s"balance ${next.balanceCents}")
    case None       => println("refused")

@main
def day11(): Unit =
  val wallet = Wallet(1000)
  printDebit(wallet, 400)
  println(s"original still ${wallet.balanceCents}")
