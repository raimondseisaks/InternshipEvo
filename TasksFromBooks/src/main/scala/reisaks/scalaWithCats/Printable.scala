package reisaks.scalaWithCats

trait Printable[A] {
  implicit def format(value: A): String
}

object PrintableInstnaces {
  implicit val PrintString = new Printable[String] {
    def format(value: String): String = value
  }
  implicit val PrintInt = new Printable[Int] {
    def format(value: Int): String = value.toString
  }
}

object PrintableSyntax {
  implicit class PrintableOps[A](value: A) {
    def format(implicit printable: Printable[A]): String = printable.format(value)
    def print(implicit printable: Printable[A]): Unit = println(format(printable))
  }
}

object Printable {
  def format[A](value: A)(implicit printable: Printable[A]): String =
    printable.format(value)
  def print[A](value: A)(implicit printable: Printable[A]): Unit =
    println(format(value))
}


import cats.{Eq, Show}
import cats.implicits._

case class Cat(name: String, age: Int, color: String)

object catsLib {
  implicit val catEq: Eq[Cat] = Eq.instance[Cat] { (cat1, cat2) =>
    cat1.name === cat2.name &&
      cat1.age === cat2.age &&
      cat1.color === cat2.color
  }

  implicit val catShow: Show[Cat] = Show.show[Cat] { cat =>
    val name = cat.name.show
    val age = cat.age.show
    val color = cat.color.show
    s"$name is a $age year old $color cat."
  }
}

object Main extends App {
  import catsLib._
  import cats.syntax.eq._
  import cats.syntax.show._

  println(Cat("Joe", 23, "Black").show)

  val cat1 = Cat("Garfield", 38, "orange and black")
  val cat2 = Cat("Heathcliff", 33, "orange and black")
  val cat3 = Cat("Garfield", 38, "orange and black")

  val optionCat1 = Option(cat1)
  val optionCat2 = Option.empty[Cat]

  println(cat1 === cat2) // false
  println(cat3 === cat1) // true
  println(optionCat2 === optionCat1) // false
}



