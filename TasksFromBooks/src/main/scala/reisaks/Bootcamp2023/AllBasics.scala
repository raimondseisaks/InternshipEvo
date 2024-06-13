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
  def GDC (num1: Int, num2: Int): Option[Int] = {
    if (num1 == 0 && num2 != 0) Some(Math.abs(num2))
    else if (num2 == 0 && num2 != 0) Some(Math.abs(num1))
    else if (num1 == 0 && num2 == 0) None
    else {
      val divisors1 = getDivisors(Math.abs(num1))
      val divisors2 = getDivisors(Math.abs(num2))
      val gdc = divisors2.find(divisors1.contains)
      gdc
    }
  }

  val getDivisors: Int => List[Int] = (num: Int) => { //extra function which get all divisors for particular number
    val r = 1 to num
    val divisors = r.filter(w => num % w == 0).sorted.reverse.toList
    divisors
  }

  def LCM (num1: Int, num2: Int): Option[Int] = {
    def mutltibleByItself(mult1: Int, mult2: Int): Option[Int] = {
      if (mult1 == mult2) Some(mult1)
      else if (mult1 == 0 || mult2 == 0) Some(0)
      else {
        if (mult1 > mult2) mutltibleByItself(mult1, mult2 + Math.abs(num2))
        else mutltibleByItself(mult1 + Math.abs(num1), mult2)
      }
    }

    if (num1 == 0 && num2 == 0) None
    else mutltibleByItself(Math.abs(num1), Math.abs(num2))
  }

}

object showBasics extends App {
  println(AllBasics.helloMethod("Scala"))

  println(AllBasics.add(3, 4))
  println(AllBasics.add2(3, 4))

  println(AllBasics.helloFunction("Scala"))

  println(AllBasics.stringLength1("First Function"))
  println(AllBasics.stringLength2("First Function"))

  println(AllBasics.GDC(5, 10))
  println(AllBasics.LCM(12, 18))
}