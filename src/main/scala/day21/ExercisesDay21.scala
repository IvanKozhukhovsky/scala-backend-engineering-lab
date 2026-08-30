package day21

import org.typelevel.doobie.*
import org.typelevel.doobie.implicits.*

// Independent exercise for scala-021.
// Implement findFill and insertFill yourself.
// Table fill: code VARCHAR PRIMARY KEY, qty INTEGER NOT NULL, cents INTEGER NOT NULL.
// Use the sql interpolator so values become `?` — do not s-interpolate or quote $code.
// Return Query0 / Update0. Do not transact, do not open a connection, do not use H2.
// Keep rateOf / convertAmount out of this file — this is SQL as a value, not a new catalog.

/** Lookup a booked fill by currency code. Building this does not talk to a database. */
def findFill(code: String): Query0[FillRow] =
  sql"select code, qty, cents from fill where code = $code".query[FillRow]

/** Insert one fill row. Column order must match FillRow: code, qty, cents. */
def insertFill(row: FillRow): Update0 =
  sql"insert into fill (code, qty, cents) values (${row.code}, ${row.qty}, ${row.cents})".update
