package Bootcamp2023.Basics
import org.scalatest.flatspec._
import org.scalatest.matchers._
import reisaks.Bootcamp2023.Basics.ControlStructuresHomework1._

class ControlStructuresHomework1Test extends AnyFlatSpec with should.Matchers{
  "isAdultIf" should "correctly calculate if person is adult" in {
    isAdultIf(5) shouldBe Right(false)
    isAdultIf(20) shouldBe Right(true)
    isAdultIf(-5) shouldBe Left("-5 is negative, we do not serve unborn people")
    isAdultIf(200) shouldBe Left("200 is too high, are you human?")
  }

  "isAdultMatch" should "correctly calculate if person is adult" in {
    isAdultMatch(5) shouldBe Right(false)
    isAdultMatch(20) shouldBe Right(true)
    isAdultMatch(-5) shouldBe Left("-5 is negative, we do not serve unborn people")
    isAdultMatch(200) shouldBe Left("200 is too high, are you human?")
  }

  "isValidTriangle" should "correctly calculate if we can generate valid triangle" in {
    isValidTriangle(-5, 2.3, 8) shouldBe false
    isValidTriangle(2, 2, 0) shouldBe false
    isValidTriangle(10, 10, 10) shouldBe true
    isValidTriangle(14, 2, 32) shouldBe false
  }

  "isValidCandidate" should "correctly calculate if candidate earned more than 10 points" in {
    isValidCandidate("Wakanda", 6, 2, true, 56) shouldBe true
    isValidCandidate("Wakanda", 2, 2, true, 56) shouldBe false
    isValidCandidate("Wakanda", 2, 2, true, 234) shouldBe false
    isValidCandidate("Riga", 5, 0, false, 1132) shouldBe false
  }
}
