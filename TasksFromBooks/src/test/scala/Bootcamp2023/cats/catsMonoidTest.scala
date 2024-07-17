package Bootcamp2023.cats

import reisaks.Bootcamp2023.cats.catsMonoid._
import cats.syntax.semigroup._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import reisaks.Bootcamp2023.cats.catsMonoid


class catsMonoidTest extends AnyFlatSpec with Matchers {

  "Int multiplication monoid" should "be correctly implemented" in {
    implicit val intMonoid = intMultiplicationMonoid
    val empty              = intMultiplicationMonoid.empty

    3 |+| 3 shouldBe 9
    2 |+| (3 |+| 3) shouldBe (2 |+| 3) |+| 3
    empty |+| 9 shouldBe 9
  }

  "String monoid" should "be correctly implemented" in {
    implicit val stringMonoid = catsMonoid.stringMonoid
    val empty                 = stringMonoid.empty

    "hello " |+| "world" shouldBe "hello world"
    "a" |+| ("b" |+| "c") shouldBe ("a" |+| "b") |+| "c"
    empty |+| "hello" shouldBe "hello"
  }

  "Boolean monoid" should "be correctly implemented" in {
    implicit val boolMonoid = catsMonoid.boolMonoid
    val empty               = boolMonoid.empty

    true |+| true shouldBe true
    true |+| false shouldBe false
    (true |+| false) |+| true shouldBe true |+| (false |+| true)

    empty |+| true shouldBe true
  }
}