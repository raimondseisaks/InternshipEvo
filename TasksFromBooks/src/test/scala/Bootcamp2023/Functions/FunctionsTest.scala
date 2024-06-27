package reisaks.Bootcamp2023.Basics
import reisaks.Bootcamp2023.Functions.Functions._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class FunctionsTest {

  class FunctionsSpec extends AnyFlatSpec with ScalaCheckDrivenPropertyChecks {

    "isEven" should "work correctly" in {
      forAll { n: Int =>
        val r = n % 2 == 0
        isEven(n) shouldBe r
        isEvenFunc(n) shouldBe r
        isEvenMethodToFunc(n) shouldBe r
      }
    }

    "mapOptionV1" should "work correctly" in {
      forAll { n: Int =>
        mapOptionV1[Int, String](Some(n), _.toString + "!") should contain(s"$n!")
      }
    }

    "mapOptionV2" should "work correctly" in {
      forAll { n: Int =>
        mapOptionV2[Int, String](Some(n), _.toString + "!") should contain(s"$n!")
      }
    }
  }
}
