package day08

class Day08Suite extends munit.FunSuite:
  test("Document implements Show and reuses showHtml"):
    assertEquals(Document("hello").show, "hello")
    assertEquals(Document("hello").showHtml, "<p>hello</p>")

  test("circumference is an extension on Circle"):
    assertEquals(Circle(0, 0, 0).circumference, 0.0)
    assertEquals(Circle(0, 0, 1).circumference, math.Pi * 2)

  test("Person.show comes from given Showable"):
    assertEquals(Person("John", "Doe").show, "John Doe")

  test("Role.show names every case"):
    assertEquals(Role.Admin.show, "admin")
    assertEquals(Role.Member.show, "member")
    assertEquals(Role.Guest.show, "guest")

  test("showAll maps with the type class in scope"):
    assertEquals(showAll(List(Role.Admin, Role.Guest)), List("admin", "guest"))
    assertEquals(showAll(List(Person("Ada", "Lovelace"))), List("Ada Lovelace"))

  test("Ticket.label implements Labelled"):
    assertEquals(Ticket(12, "Fix login").label, "#12 Fix login")
    assertEquals(Ticket(1, "Hi").label, "#1 Hi")

  test("asNonEmpty trims or becomes None"):
    assertEquals("ada".asNonEmpty, Some("ada"))
    assertEquals("  ada  ".asNonEmpty, Some("ada"))
    assertEquals("".asNonEmpty, None)
    assertEquals("   ".asNonEmpty, None)

  test("Status.show names every case"):
    assertEquals(Status.Open.show, "open")
    assertEquals(Status.Closed.show, "closed")

  test("User.show formats name and age"):
    assertEquals(User("Ada", 36).show, "Ada, 36")

  test("shown delegates to the type class, not toString"):
    assertEquals(shown(Status.Open), "open")
    assertEquals(shown(User("Ada", 36)), "Ada, 36")
    assertNotEquals(shown(Status.Open), Status.Open.toString)
    assertNotEquals(shown(User("Ada", 36)), User("Ada", 36).toString)
