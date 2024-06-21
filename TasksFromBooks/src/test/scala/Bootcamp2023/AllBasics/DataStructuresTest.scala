package Bootcamp2023.Basics
import org.scalatest.flatspec._
import org.scalatest.matchers._
import reisaks.Bootcamp2023.Basics.DataStructures._

class DataStructuresTest extends AnyFlatSpec with should.Matchers{
  "applyNTimesForInts" should "correctly calculate function for number x n times" in {
    allEqual(List(1,1,1,1)) shouldBe true
    allEqual(List(1,1,1,2)) shouldBe false
    allEqual(List()) shouldBe true
    allEqual(List(1)) shouldBe true
  }

  "totalVegetableCost" should "be correct" in {
    totalVegetableCosts shouldEqual 5012
  }

  "totalVegetableWeights" should "be correct" in {
    totalVegetableWeights shouldEqual Map(
      "cucumbers" -> 6460,
      "olives"    -> 64,
    )
  }

  "allSubSetsOfSizeN" should "work correctly on 2 from Set(1, 2, 3)" in {
    allSubsetsOfSizeN(Set(1, 2, 3), 2) shouldEqual Set(Set(1, 2), Set(2, 3), Set(1, 3))
  }

  "sort considering equal values" should "be correct on example 1" in {
    val input    = Map("a" -> 1, "b" -> 2, "c" -> 4, "d" -> 1, "e" -> 0, "f" -> 2, "g" -> 2)
    val expected = List(Set("e") -> 0, Set("a", "d") -> 1, Set("b", "f", "g") -> 2, Set("c") -> 4)
    val obtained = sortConsideringEqualValues(input)
    obtained shouldEqual expected
  }

  it should "be correct on example 2" in {
    val values = Set("a1", "a2", "b1", "c1", "c2", "d1").map { x =>
      x -> x.head.toInt
    }.toMap

    sortConsideringEqualValues(values) shouldEqual List(
      Set("a1", "a2") -> 'a'.toInt,
      Set("b1")       -> 'b'.toInt,
      Set("c1", "c2") -> 'c'.toInt,
      Set("d1")       -> 'd'.toInt,
    )
  }
}
