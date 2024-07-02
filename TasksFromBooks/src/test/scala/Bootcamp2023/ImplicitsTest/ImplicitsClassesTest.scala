package Bootcamp2023.ImplicitsTest
import org.scalatest.funsuite.AnyFunSuite
import reisaks.Bootcamp2023.Implicits.ImplicitClasses._
import java.time.Instant

class ImplicitsClassesTest extends AnyFunSuite {
  // we will not use tests these time, but you are welcome to implement them
  // and do a pull request
  test("Excersise 1: pow") {
    assert(EvolutionUtils0.pow(2, 0) == 1)
    assert(EvolutionUtils0.pow(2, 1) == 2)
    assert(EvolutionUtils0.pow(2, 2) == 4)
    assert(EvolutionUtils0.pow(2, 10) == 1024)
    assert(EvolutionUtils0.pow(2, -1) == 0)
    assert(EvolutionUtils0.pow(2, -2) == 0)
    assert(EvolutionUtils0.pow(3, 2) == 9)
  }

  test("Excersise 2: concat") {
    assert(EvolutionUtils0.concat(12,23) == 1223)
    assert(EvolutionUtils0.concat(2, 1) == 21)
    assert(EvolutionUtils0.concat(20, 14) == 2014)
    assert(EvolutionUtils0.concat(2, 10) == 210)
  }

  test("Excersise 3: parse string to Instant") {
    assert(EvolutionUtils0.toInstant("abc").isEmpty)
    assert(EvolutionUtils0.toInstant("2023-07-02T12:34:56Z").contains(Instant.parse("2023-07-02T12:34:56Z")))
  }


  test("Excersise 4: mean of list of ints") {
    assert(EvolutionUtils0.mean(List(1,2,3,4,5)) == 3)
    assert(EvolutionUtils0.mean(List()) == 0)
    assert(EvolutionUtils0.mean(List(1, -1)) == 0)
  }

  test("Excersise 5: first test of implicits") {
    import EvolutionUtils4._
    assert(4.pow(2) == 16)
    assert((2 pow 2) == 4)
    assert((12 concat 12) == 1212)
  }

}



