package reisaks.Bootcamp2023.iotf_practise

import cats.{Monad, Show}
import cats.effect.std.Console
import cats.syntax.all._
import cats.effect.{IO, Sync}

import scala.util.Try
import scala.util.control.NonFatal

object ex4 {
  case class Message(value: String)

  trait Send[F[_]] {

    /** Send the message to all specified players by their ids
     * @return
     *   may return connection errors
     */
    def send(ids: Set[Long], msg: Message): F[Unit]
  }

  trait AllIdsCache[F[_]] {

    /** @return
     *   always returns set of ids of players
     */
    def get: F[Set[Long]]
  }

  /*
   * We also have additional traits to be able handle and raise specific errors
   */
  trait Raise[F[_], E] {
    def raise(error: E): F[Unit]
  }
  trait Handle[F[_], E] {
    def handle[A](fa: F[A]): F[Either[E, A]]
  }

  /*
   * We have special Error class which will be handled at the end of our service
   */
  case class ApiError(status: Int, message: String) extends Throwable
  object ApiError {
    def from(error: Throwable): ApiError = ApiError(500, error.getMessage)
  }

  /** Sends messages to players. Handles all connection errors, logs them, and can return only ApiErrors
   */
  trait SendTo[F[_]] {
    def toEveryone(msg: Message): F[Unit]
    def toPlayer(id: Long, msg: Message): F[Unit]
  }

  object SendTo {
    /*
     * Task: improve SendTo implementation to complete conditions from the comment
     */
    def make[F[_]: Monad: Console](
                           cache: AllIdsCache[F],
                           send: Send[F],
                         )(implicit handle: Handle[F, Throwable], raise: Raise[F, ApiError]): SendTo[F] =
      new SendTo[F] {
        def toEveryone(msg: Message): F[Unit] =
          cache.get.flatMap { ids =>
            sendSafe(ids, msg)
          }

        def toPlayer(id: Long, msg: Message): F[Unit] =
          sendSafe(Set(id), msg)

        def sendSafe(ids: Set[Long], msg: Message): F[Unit] =
          handle.handle(send.send(ids, msg)).flatMap {
            case Left(error) => Console[F].errorln(s"error $error") *> raise.raise(ApiError.from(error))
            case Right(_) => Monad[F].unit
          }
      }
  }

  /*
   * Homework: write a test for `SendTo` service using `cats.IO` and `Either[Throwable, *]`
   * You will have to implement `Handle` and `Raise` for them and test that `SendTo` uses them correctly
   */

  implicit def ioHandle(implicit F: Sync[IO]): Handle[IO, Throwable] = new Handle[IO, Throwable] {
    def handle[A](fa: IO[A]): IO[Either[Throwable, A]] = fa.attempt
  }

  implicit def ioRaise(implicit F: Sync[IO]): Raise[IO, ApiError] = new Raise[IO, ApiError] {
    def raise(error: ApiError): IO[Unit] = F.raiseError(error)
  }

  implicit def eitherHandle: Handle[Either[Throwable, *], Throwable] = new Handle[Either[Throwable, *], Throwable] {
    def handle[A](fa: Either[Throwable, A]): Either[Throwable, Either[Throwable, A]] = Right(fa)
  }

  implicit def eitherRaise: Raise[Either[Throwable, *], ApiError] = new Raise[Either[Throwable, *], ApiError] {
    def raise(error: ApiError): Either[Throwable, Unit] = Left(error)
  }


  implicit val eitherConsole: Console[Either[Throwable, *]] = new Console[Either[Throwable, *]] {
    override def readLineWithCharset(charset: java.nio.charset.Charset): Either[Throwable, String] =
      Right(scala.io.StdIn.readLine())

    override def readLine: Either[Throwable, String] =
      Right(scala.io.StdIn.readLine())

    override def print[A](a: A)(implicit S: Show[A]): Either[Throwable, Unit] =
      Right(Predef.print(S.show(a)))

    override def println[A](a: A)(implicit S: Show[A]): Either[Throwable, Unit] =
      Right(Predef.println(S.show(a)))

    override def error[A](a: A)(implicit S: Show[A]): Either[Throwable, Unit] =
      Try(System.err.print(S.show(a))) match {
        case _ => Right(())
        case NonFatal(e) => Left(e)
      }

    override def errorln[A](a: A)(implicit S: Show[A]): Either[Throwable, Unit] =
        Try(System.err.println(S.show(a))) match {
          case _ => Right(())
          case NonFatal(e) => Left(e)
        }
  } //made this because Console does not have implementation for Either[Throwable, *]


}
