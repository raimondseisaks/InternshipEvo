package reisaks.Bootcamp2023.Implicits

object ContextBounds extends App {
  object Exercise1{

    def concat(a: Int, b: Int): Int = s"$a$b".toInt

    // Exercise 1: Implement repeat functions for various types.
    // For `Int` using `concat` function above. For others, use built-in methods.

    // `repeat(72, 3)` should return `727272`
    def repeat(a: Int, times: Int): Int = {
      def recursiveImp(b: Int, times: Int): Int = {
        if (times == 1) b
        else recursiveImp(concat(b, a), times - 1)
      }
      recursiveImp(a, times)
    }

    // `repeat("Scala", 3)` should return `"ScalaScalaScala"`
    def repeat(a: String, times: Int): String = (1 until times).foldLeft(a) { case (acc, _ ) => acc ++ a}

    // `repeat(List(10, 20, 30), 3)` should return `List(10, 20, 30, 10, 20, 30, 10, 20, 30)`
    def repeat(a: List[Int], times: Int): List[Int] = (1 until times).foldLeft(a) { case (acc, _ ) => acc ++ a}
  }

  // Exercise 3:
  // - Implement `Concatenable` so it works for `Int`, `String` and `List[Int]`.
  // - Now implement `repeat` function using the new trait.

  trait Concatenable[T] {
    def concat(a: T, b: T): T
  }
  object Concatenable {
    implicit val forInt: Concatenable[Int]           = new Concatenable[Int] {
      def concat(a: Int, b: Int): Int = s"$a$b".toInt
    }
    implicit val forString: Concatenable[String]     = new Concatenable[String] {
      def concat(a: String, b: String): String = s"$a$b"
    }
    implicit val forListInt: Concatenable[List[Int]] = new Concatenable[List[Int]] {
      def concat(a: List[Int], b: List[Int]): List[Int] = a++b
    }
  }

  object Exercise3 {
    def repeat[T](a: T, times: Int, concatenable: Concatenable[T]): T =
      (1 until times).foldLeft(a) { case(acc, _ ) => concatenable.concat(acc, a)}
  }

  // Exercise 4:
  // - Make the `repeat` method above more convenient to use by moving
  //   `Concatenable` to implicit parameter block.
  // - Make implicits for `Int`, `String` and `List[Int]` available by making
  //   the vals in `Concatenable` companion object implicit.
  object Exercise4 {
    def repeat[T](a: T, times: Int)(implicit concatenable: Concatenable[T]): T =
      (1 until times).foldLeft(a) { case(acc, _) => concatenable.concat(acc, a)}

  }

  // Exercise 5: implement the methods using `repeat` we just made.
  object Exercise5 {

    def repeatTenTimes[T](a: T)(implicit concatenable: Concatenable[T]): Unit                           = Exercise4.repeat(a,10)
    def repeatTenTimesIfTrue[T](condition: Boolean)(a: T)(implicit concatenable: Concatenable[T]): Unit = if (condition) Exercise4.repeat(a, 10)

  }

  def method1[T](a: T)(implicit concatenable: Concatenable[T]): Unit = ()
  def method2[T: Concatenable](a: T): Unit                           = ()

  // Exercise 6: Use context bound to tidy up `repeat` method above
  object Exercise6 {
     def repeat[T: Concatenable](a: T, times: Int): T =
       Exercise4.repeat(a, times)
  }

  // Exercise 7: Use the same approach to create syntax for `concat`
  // I.e. this should compile:
  // 72.concat(651)
  object Exercise7 {
    implicit class RepeatSyntax[T](a: T) {
      def repeat(times: Int)(implicit concatenable: Concatenable[T]): T =
        Exercise4.repeat(a, times)

      def concat(b: T)(implicit concatenable: Concatenable[T]): T =
        concatenable.concat(a,b)
    }

    72.repeat(3)
    List(10, 20, 30).repeat(3)
    72.concat(651)

  }

}
