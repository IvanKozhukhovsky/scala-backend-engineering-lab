package day05

class Day05Suite extends munit.FunSuite:
  private val users =
    List(
      User("1", "Ada", Some("ada@example.com")),
      User("2", "Grace", None)
    )

  test("findUser returns Some when the id exists"):
    assertEquals(findUser(users, "1").map(_.name), Some("Ada"))

  test("findUser returns None when the id is absent"):
    assertEquals(findUser(users, "9"), None)

  test("displayName uses a fallback for None"):
    assertEquals(displayName(Some(users.head)), "Ada")
    assertEquals(displayName(None), "guest")

  test("localPart maps inside Some and preserves None"):
    assertEquals(localPart(Some("ada@example.com")), Some("ada"))
    assertEquals(localPart(None), None)

  test("primaryEmail is None if the user or the email is missing"):
    assertEquals(primaryEmail(users, "1"), Some("ada@example.com"))
    assertEquals(primaryEmail(users, "2"), None)
    assertEquals(primaryEmail(users, "9"), None)

  private val accounts =
    List(
      Account("1", "Ada", Some("ada@example.com")),
      Account("2", "Grace", None),
      Account("3", "Bob", Some("bob@example.com"))
    )

  test("findAccount returns Some for the first matching id"):
    assertEquals(findAccount(accounts, "3").map(_.owner), Some("Bob"))

  test("findAccount returns None when the id is absent"):
    assertEquals(findAccount(accounts, "9"), None)
    assertEquals(findAccount(Nil, "1"), None)

  test("ownerName unwraps Some and falls back for None"):
    assertEquals(ownerName(Some(Account("1", "Ada", None))), "Ada")
    assertEquals(ownerName(None), "unknown")

  test("emailDomain takes the substring after the first @"):
    assertEquals(emailDomain(Some("ada@example.com")), Some("example.com"))
    assertEquals(emailDomain(Some("123@321@example.com")), Some("321@example.com"))
    assertEquals(emailDomain(Some("@example.com")), Some("example.com"))

  test("emailDomain keeps None and the whole string when @ is absent"):
    assertEquals(emailDomain(None), None)
    assertEquals(emailDomain(Some("123example.com")), Some("123example.com"))
    assertEquals(emailDomain(Some("")), Some(""))

  test("greeting uses match for present and absent accounts"):
    assertEquals(greeting(Some(Account("1", "Ada", None))), "Hello, Ada")
    assertEquals(greeting(None), "Hello, guest")
