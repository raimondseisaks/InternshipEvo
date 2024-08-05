package Bootcamp2023.iotf_practise
import reisaks.Bootcamp2023.iotf_practise._
import cats.effect.IO
import org.scalatest.matchers.should.Matchers

import cats.effect.unsafe.implicits.global
import org.scalatest.funsuite.AnyFunSuite

class ex4Test extends AnyFunSuite with Matchers {

  import ex4._

  test("SendTo toEveryone using IO") {
    val cache = new AllIdsCache[IO] {
      def get: IO[Set[Long]] = IO.pure(Set(1L, 2L, 3L))
    }

    val send = new Send[IO] {
      def send(ids: Set[Long], msg: Message): IO[Unit] = IO.raiseError(new Exception("Connection error"))
    }
    val sendTo = SendTo.make[IO](cache, send)

    val result = sendTo.toEveryone(Message("Hello")).attempt.unsafeRunSync()
    val result2 = sendTo.toPlayer(2L, Message("Sup")).attempt.unsafeRunSync()


    result shouldBe Left(ApiError(500, "Connection error"))
    result2 shouldBe Left(ApiError(500, "Connection error"))
  }

  test("Service with IO works fine") {
    val cache = new AllIdsCache[IO] {
      def get: IO[Set[Long]] = IO.pure(Set(1L, 2L, 3L))
    }

    val send = new Send[IO] {
      def send(ids: Set[Long], msg: Message): IO[Unit] = IO.apply()
    }

    val sendTo = SendTo.make[IO](cache, send)

    val result = sendTo.toEveryone(Message("Hello")).attempt.unsafeRunSync()
    val result2 = sendTo.toPlayer(2L, Message("Sup")).attempt.unsafeRunSync()


    result shouldBe Right()
    result2 shouldBe Right()
  }

  test("SendTo toEveryone using Either") {
    val cache = new AllIdsCache[Either[Throwable, *]] {
      def get: Either[Throwable,Set[Long]] = Right(Set(1L, 2L, 3L))
    }
    val send = new Send[Either[Throwable, *]] {
      def send(ids: Set[Long], msg: Message): Either[Throwable, Unit] = Left(ApiError(500, "Connection error"))
    }

    val sendTo = SendTo.make[Either[Throwable, *]](cache, send)

    val res = sendTo.toEveryone(Message("Hello"))
    val res2 = sendTo.toPlayer(1L, Message("Yo"))

    res shouldBe Left(ApiError(500, "Connection error"))
    res2 shouldBe Left(ApiError(500, "Connection error"))
  }

  test("Service with Either works fine") {
    val cache = new AllIdsCache[Either[Throwable, *]] {
      def get: Either[Throwable,Set[Long]] = Right(Set(1L, 2L, 3L))
    }
    val send = new Send[Either[Throwable, *]] {
      def send(ids: Set[Long], msg: Message): Either[Throwable, Unit] = Right()
    }

    val sendTo = SendTo.make[Either[Throwable, *]](cache, send)

    val res = sendTo.toEveryone(Message("Hello"))
    val res2 = sendTo.toPlayer(1L, Message("Yo"))


    res shouldBe Right()
    res2 shouldBe Right()
  }


}