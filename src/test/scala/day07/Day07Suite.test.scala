package day07

class Day07Suite extends munit.FunSuite:
  test("describeRole names every role"):
    assertEquals(describeRole(Role.Admin), "full access")
    assertEquals(describeRole(Role.Member), "limited access")
    assertEquals(describeRole(Role.Guest), "read only")

  test("validPort is Right only inside 1 to 65535"):
    assertEquals(validPort(1), Right(1))
    assertEquals(validPort(65535), Right(65535))
    assertEquals(validPort(0), Left(PortError.OutOfRange(0)))
    assertEquals(validPort(65536), Left(PortError.OutOfRange(65536)))

  test("readPort distinguishes parse failure from range failure"):
    assertEquals(readPort("443"), Right(443))
    assertEquals(readPort(" 80 "), Right(80))
    assertEquals(readPort("foo"), Left(PortError.NotAnInteger("foo")))
    assertEquals(readPort("0"), Left(PortError.OutOfRange(0)))

  test("describePortError unwraps the payload"):
    assertEquals(describePortError(PortError.NotAnInteger("foo")), "not an integer: foo")
    assertEquals(describePortError(PortError.OutOfRange(0)), "out of range: 0")

  test("label names every ticket status"):
    assertEquals(label(TicketStatus.Open), "open")
    assertEquals(label(TicketStatus.InReview), "in review")
    assertEquals(label(TicketStatus.Done), "done")

  test("register checks empty name before age"):
    assertEquals(register("Ada", 18), Right("Ada"))
    assertEquals(register(" Ada ", 21), Right("Ada"))
    assertEquals(register("   ", 21), Left(SignupError.EmptyName))
    assertEquals(register("", 17), Left(SignupError.EmptyName))
    assertEquals(register("Ada", 17), Left(SignupError.TooYoung(17)))

  test("explain matches both signup errors"):
    assertEquals(explain(SignupError.EmptyName), "name is empty")
    assertEquals(explain(SignupError.TooYoung(12)), "too young: 12")
