package reisaks.Bootcamp2023.Functions
import java.time.Instant
import scala.util.Try

object Functions {
  // Exercise. Implement `isEven` method that checks if a number is even.
  def isEven(n: Int): Boolean = n % 2 == 0

  // Exercise. Implement `isEvenFunc` function that behaves exactly like `isEven` method.
  val isEvenFunc: Int => Boolean = _ % 2 == 0 // n => n % 2 == 0

  // Exercise. Implement `isEvenMethodToFunc` function by transforming `isEven` method into a function.
  val isEvenMethodToFunc: Int => Boolean = isEven // n => isEven(n)

  // Exercise. Implement `mapOption` function without calling `Option` APIs.
  def mapOptionV1[A, B](option: Option[A], f: A => B): Option[B] = option.map(f)

  def mapOptionV2[A, B](option: Option[A], f: A => B): Option[B] = {
    option match {
      case Some(value) => Some(f(value))
      case _ => None
    }
  }

  def parseDate(s: String): Instant = Instant.parse(s)
  def parseDatePure(s: String): Option[Instant] = Try(parseDate(s)).toOption

  def divide(a: Int, b: Int): Int     = a / b
  def dividePure(a: Int, b: Int): Either[String, Int] =
    b match {
      case 0 => Left("Division by zero")
      case _ => Right(a / b)
    }

  def isAfterNow(date: Instant): Boolean   = date.isAfter(Instant.now())
  def isAfterNowPure(date: Instant, now: Instant): Boolean = date.isAfter(now)

  case class NonEmptyList[T](head: T, rest: List[T])
  def makeNonEmptyList[T](list: List[T]): NonEmptyList[T] = {
    if (list.isEmpty) println("Error: list must not be empty")
    NonEmptyList(list.head, list.tail)
  }
  def makeNonEmptyListPure[T](list: List[T]): Either[String, NonEmptyList[T]] = {
    if (list.isEmpty) {
      Left("Error: list must not be empty")
    } else {
      Right(NonEmptyList(list.head, list.tail))
    }
  }

}
