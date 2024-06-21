package reisaks.Bootcamp2023

object AllBasics  {
  def helloMethod(name: String): String =
    s"Hello, $name!"

  def add(a: Int, b: Int): Int = a+b

  val helloFunction: String => String = (name: String) => helloMethod(name)

  val stringLength1: String => Int = (word: String) => word.length

  //If each argument of a function is used once
  val stringLength2: String => Int = _.length
  val add2: (Int, Int) => Int = _ + _

  def power(n: Byte)(x: Int): Int => Long =
    x => math.pow(x, n).toLong

  // Hometask Greatest comon divisor and least common multiple
  def GDC (num1: Int, num2: Int): Int = {
    (Math.abs(num1), Math.abs(num2)) match {
      case (0, 0) => -1
      case (num1, num2) if num2 == 0 => num1
      case (num1, num2) if num1 == 0 => num2
      case (num1, num2) => if (num1 > num2) GDC(num1%num2, num2) else GDC(num1, num2%num1)
    }
  }

  def LCM (num1: Int, num2: Int): Int = {
    (math.abs(num1), math.abs(num2)) match {
      case (0, 0) => -1
      case (num1, num2) if (num1 == 0 || num2 == 0) => 0
      case (num1, num2) => num1*(num2)/GDC(num1, num2)
    }
  }

}
