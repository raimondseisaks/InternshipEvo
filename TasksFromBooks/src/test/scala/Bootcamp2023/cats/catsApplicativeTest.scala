package Bootcamp2023.cats

import cats.syntax.applicative._
import cats.syntax.option._
import reisaks.Bootcamp2023.cats.catsApplicative._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class catsApplicativeTest extends AnyFlatSpec with Matchers {
  "EvoApplicative for Option" should "be implemented correctly" in {
    val pureOpt = optionApplicative.pure(40)
    pureOpt shouldBe 40.some
    pureOpt shouldBe 40.pure[Option]

    optionApplicative.ap[Int, String](Some(_.toString))(15.some) shouldBe Some("15")
  }
}
