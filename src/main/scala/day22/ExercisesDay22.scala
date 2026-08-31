package day22

import cats.effect.IO
import org.typelevel.doobie.*
import org.typelevel.doobie.implicits.*

// Independent exercise for scala-022.
// Implement fillH2Xa and FillRepository yourself.
// fillH2Xa: org.h2.Driver, jdbc:h2:mem:$name;DB_CLOSE_DELAY=-1, user sa, empty password,
// logHandler = None. Unique `name` so tests do not share a catalog.
// FillRepository: transact findFill(...).option and insertFill(...).run.
// Do not put JDBC inside rateOf. Do not call postgresPool. Do not quote $code.
// Do not use unique when the row may be missing. Do not start Flyway.

/** In-process JDBC for the fill table. Building this does not create the table. */
def fillH2Xa(name: String): Transactor[IO] =
  Transactor.fromDriverManager[IO](
    driver = "org.h2.Driver",
    url = s"jdbc:h2:mem:$name;DB_CLOSE_DELAY=-1",
    user = "sa",
    password = "",
    logHandler = None
  )

/** JDBC rim for booked fills. Domain stays `rateOf`. */
final class FillRepository(xa: Transactor[IO]):
  def find(code: String): IO[Option[FillRow]] =
    findFill(code).option.transact(xa)

  def insert(row: FillRow): IO[Int] =
    insertFill(row).run.transact(xa)
