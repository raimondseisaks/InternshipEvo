package Bootcamp2023.Basics
import org.scalatest.flatspec._
import org.scalatest.matchers._
import reisaks.Bootcamp2023.Basics.ControlStructures._


class ControlStructuresTest extends AnyFlatSpec with should.Matchers {
  "applyNTimesForInts" should "correctly calculate function for number x n times" in {
    applyNTimesForInts(_ + 1, 4)(3) shouldBe 7
  }

  "polymorphicApplyForIntsV1" should "correctly calculate function for number x n times" in {
    polymorphicApplyForIntsV1((x: Int) => x + 1, 4)(3) shouldBe 7
  }

  "polymorphicApplyForIntsV2" should "correctly calculate function for number x n times" in {
    polymorphicApplyForIntsV2((x: Double) => x + 2.0, 4)(3) shouldEqual  11.0
  }

}
