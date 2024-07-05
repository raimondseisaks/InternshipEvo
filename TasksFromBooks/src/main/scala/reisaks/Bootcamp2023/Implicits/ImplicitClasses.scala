package reisaks.Bootcamp2023.Implicits
import java.time.Instant
import scala.util.Try

object ImplicitClasses {

  object EvolutionUtils0 {

    // Exercise 1:
    // Implement a `pow` method which calculates a power of number.
    //
    // I.e. `pow(4, 2) == 1` and `pow(3, 3) == 27`.
    def pow(base: Int, exponent: Int): Int = Math.pow(base, exponent).toInt

    // Exercise 2:
    // Implement a concat method which concatenates two positive `Int`
    // numbers into one.
    //
    // I.e. `concat(72, 456) == 72456`.
    def concat(a: Int, b: Int): Int = (a.toString + b.toString).toInt //we accept that a and b must be positive

    // Exercise 3:
    // Implement a `toInstant` method which tries to parse a String
    // to a standard JVM instant representation.
    def toInstant(string: String): Option[Instant] = Try(Instant.parse(string)).toOption

    // Exercise 4:
    // Implement a `mean` method which calculates an average number.
    def mean(list: List[Int]): Int = {
      if (list.isEmpty) 0
      else list.sum / list.length
    }

  }

  object EvolutionUtils4 {

    implicit class RichInt(a: Int) {
      def pow(exponent: Int): Int = EvolutionUtils0.pow(a, exponent)   //I used EvolutionUtils0 object for non-duplicate code purposes
      def concat(b: Int): Int     = EvolutionUtils0.concat(a,b)
    }
    implicit class RichString(a: String) {
      def toInstant: Option[Instant] = EvolutionUtils0.toInstant(a)
    }
    implicit class RichListInt(list: List[Int]) {
      def mean: Int = EvolutionUtils0.mean(list)
    }

  }
  {
    import EvolutionUtils4._

    // Exercise 5:
    // Use the new method directly on type without using a wrapper:
    4.pow(2)
    2 pow 12
    72.concat(456)
    List(1, 2, 3, 4, 5).mean
}
}
