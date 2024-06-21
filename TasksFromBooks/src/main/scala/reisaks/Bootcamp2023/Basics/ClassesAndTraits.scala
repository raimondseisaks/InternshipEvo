package reisaks.Bootcamp2023.Basics


trait HasBalance {
  def balance: Double
}

final case class Stack[A](elements: List[A] = Nil) {    //Added simple data validation using either data type
  def push(elem: A): Stack[A] = Stack(elements.prepended(elem))
  def peek: Either[String, A] =
    if (elements.isEmpty) Left("Stack is empty")
  else Right(elements.head)
  def pop: Either[String, (A, Stack[A])] = if (elements.isEmpty) Left("Stack is empty") else Right(elements.head, Stack(elements.tail))
}

trait Account extends HasBalance {
  // def addMoney(amount: Double)
  // def takeMoney(amount: Double)
}

sealed trait User {
  def login: String
}

final case class RegularUser(login: String, balance: Double) extends User with Account


object ClassesAndTraits extends App {
  def totalBalance(accounts: List[HasBalance]): HasBalance = {
    new HasBalance {
      def balance: Double = accounts.foldLeft(0.0)((sum, w) => sum + w.balance)
    }
  }

  def fixxBuzz(n: Int): String = {
    if (n % 15 == 0) "fizzbuzz"
    else if (n % 3 == 0) "fizz"
    else if (n % 5 == 0) "buzz"
    else s"$n"   // or .toString
  }

  def fizzBuzz2(n: Int): String =
    n match {
      case n if (n % 15 == 0) => "fizzbuzz"
      case n if (n % 3 == 0) => "fizz"
      case n if (n % 5 == 0) => "buzz"
      case _ => n.toString
    }
  }

