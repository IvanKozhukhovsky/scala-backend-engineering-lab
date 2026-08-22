package day08

trait Show:
  def show: String
  def showHtml: String = s"<p>$show</p>"

case class Document(text: String) extends Show:
  def show: String = text

case class Circle(x: Double, y: Double, radius: Double)

extension (c: Circle) def circumference: Double = c.radius * math.Pi * 2

trait Showable[A]:
  extension (a: A) def show: String

case class Person(firstName: String, lastName: String)

given Showable[Person] with
  extension (p: Person)
    def show: String =
      s"${p.firstName} ${p.lastName}"

enum Role:
  case Admin, Member, Guest

given Showable[Role] with
  extension (r: Role)
    def show: String =
      r match
        case Role.Admin  => "admin"
        case Role.Member => "member"
        case Role.Guest  => "guest"

def showAll[A: Showable](as: List[A]): List[String] =
  as.map(_.show)

@main
def day08(): Unit =
  println(s"Document: ${Document("hello").showHtml}")
  println(s"Circumference: ${Circle(0, 0, 5).circumference}")
  println(s"Person: ${Person("John", "Doe").show}")
  println(s"Roles: ${showAll(List(Role.Admin, Role.Guest))}")
