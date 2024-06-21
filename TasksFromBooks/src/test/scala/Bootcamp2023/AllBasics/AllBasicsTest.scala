package Bootcamp2023.AllBasics
import org.scalatest.flatspec._
import org.scalatest.matchers._
import reisaks.Bootcamp2023.AllBasics._


class AllBasicsTest extends AnyFlatSpec with should.Matchers {
  "GDC" should "find GDC from x and y" in {
    GDC(5, 10) shouldEqual 5
    GDC(0,5) shouldEqual 5
    GDC(12, 18) shouldEqual 6
    GDC(-12, -18) shouldEqual 6
  }

  "LCM" should "find LCM from x and y" in {
    LCM(2, 3) shouldEqual 6
    LCM(0,5) shouldEqual 0
  }
}
