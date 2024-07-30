package reisaks.Bootcamp2023.iotf_practise

import cats.effect.{Async, Deferred, IO}
import cats.effect.kernel.Sync
import reisaks.Bootcamp2023.typeclass.TypeClassesExamples.Applicative
import zio.{Task, ZIO}

import scala.concurrent.Future

object ex3 {
  case class User(id: Long, login: String)

  /*
   * Old service from the company-common library
   */
  trait UnsafeUserRepository {
    def findUser(id: Long): Future[Option[User]]
    def addUser(user: User): Future[Unit]
  }

  trait UserRepository[F[_]] {
    def findUser(id: Long): F[Option[User]]
    def addUser(user: User): F[Unit]
  }
  object UserRepository {
    /*
     * Task: implement so that the service can be used from both `cats.IO` and `zio.Task`
     */
    def make[F[_]: FromFuture](unsafe: UnsafeUserRepository): UserRepository[F] =
      new UserRepository[F] {
        def findUser(id: Long): F[Option[User]] = FromFuture[F].apply(unsafe.findUser(id))
        def addUser(user: User): F[Unit]        = FromFuture[F].apply(unsafe.addUser(user))
      }
  }

  trait FromFuture[F[_]] {
    def apply[A](f: => Future[A]): F[A]
  }
  object FromFuture {
    def apply[F[_]](implicit a: FromFuture[F]): FromFuture[F] = a
  }


  implicit val fromFutureIo = new FromFuture[IO] {
    override def apply[A](f: => Future[A]): IO[A] = futureToIO(f)
  }

  implicit val fromFuture = new FromFuture[Task] {
    override def apply[A](f: => Future[A]): Task[A] = futureToZIO(f)
  }
  // Hint:
  def futureToIO[A](f: => Future[A]): IO[A]    = IO.fromFuture(IO.delay(f))
  def futureToZIO[A](f: => Future[A]): Task[A] = ZIO.fromFuture(_ => f)

  /*
   * Bonus task: make it possible to create the service from `cats.Id` for testing purposes
   */
}
