package reisaks.Bootcamp2023

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

  val entity1 = new HasBalance { def balance: Double = 100 }
  val entity2 = new HasBalance { def balance: Double = 200 }
  val entity3 = new HasBalance { def balance: Double = 500 }

  println(totalBalance(List(entity1, entity2, entity3)).balance) /// 800.0

  val stack = Stack(List(4,3,2,1))
  val stack2 = Stack[Int](List())
  println(stack.push(5))    // Stack(List(5, 4, 3, 2, 1))
  println(stack.peek)       // 4
  println(stack.pop) // (4,Stack(List(3, 2, 1)))

  println(stack2.peek) // Left(Stack is empty)
  println(stack2.pop)  // Left(Stack is empty)
  println(stack2.push(3)) //Stack(List(3))

  println(fixxBuzz(15))  //FizzBuzz

  println(fizzBuzz2(15)) //FizzBuzz
  }

