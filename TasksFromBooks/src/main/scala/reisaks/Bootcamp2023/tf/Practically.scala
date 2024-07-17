package reisaks.Bootcamp2023.tf

import cats._
import cats.implicits._
import cats.effect._
import cats.effect.std.{Console, Random}

object Practically extends App {
  object Exercise1 {
    final case class User(id: Int, age: Int)

    trait FindUser[F[_]] {
      def apply(id: Int): F[User]
    }

    def findFakeUserLocal[F[_]: Monad]: FindUser[F] =
      new FindUser[F] {
        def apply(id: Int): F[User] =
          Applicative[F].pure(User(id, (id * 7 * 5) % 97))
      }

    // assume precisely users with ids 1..100 exist in our imaginary database
    // users must have different ids
    def findAgeMatch[F[_]: Monad](findUser: FindUser[F]): F[Option[(User, User)]] = {
      val ids = (1 to 100).toList
      for {
        users <- ids.traverse(findUser.apply)
        matchedPairs = for {
          user1 <- users
          user2 <- users
          if user1.age == user2.age && user1.id != user2.id
        } yield (user1, user2)
        result <- Applicative[F].pure(matchedPairs.headOption)
      } yield result
    }

    def main: Option[Unit] = {
      val findUser = findFakeUserLocal[Option]
      for {
        result <- findAgeMatch(findUser)
        _ = println(s"findBirthdayMatch: $result")
      } yield ()
    }
  }

  object Exercise2 {
    // lecture notes: mention parametric

    trait BasicRandom[F[_]] {
      def nextIntBounded(i: Int): F[Int]
    }

    object BasicRandom {
      def fromRandom[F[_]](random: Random[F]): BasicRandom[F] =
        new BasicRandom[F] {
          def nextIntBounded(i: Int): F[Int] = random.nextIntBounded(i)
        }
    }

    // def largestOfThree[F[_]: Applicative: BasicRandom]: F[Int] =
    def largestOfThree[F[_]: Applicative](implicit
                                          random: BasicRandom[F]
                                         ): F[Int] =
      (
        random.nextIntBounded(100),
        random.nextIntBounded(100),
        random.nextIntBounded(100),
      ).mapN { case (a, b, c) =>
        List(a, b, c).max
      }

    def main: IO[Unit] =
      for {
        random         <- Random.scalaUtilRandom[IO].map(BasicRandom.fromRandom)
        largestOfThree <- largestOfThree[IO](implicitly[Applicative[IO]], random)
        line2           = s"largestOfThree: ${largestOfThree}"
        _              <- Console[IO].println(line2)
      } yield ()
  }

  Exercise1.main
}