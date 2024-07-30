package reisaks.Bootcamp2023.iotf_practise

//import akka.stream.impl.io.OutputStreamSourceStage.Send
import cats.Monad
import cats.effect.IO
import cats.implicits._
object Intro {

  /*
   * Let's look at a more complicated example:
   * Question: what can we say about their operation in the first and second cases?
   */
  object `1` {
    trait Log {
      def info(message: String): IO[Unit]
    }

    case class Response[A](status: Int, data: A)
    trait Send {
      def toEveryone(id: Long): IO[Response[Boolean]]
    }

    def logAndSend(id: Long, log: Log, send: Send): IO[Boolean] = {
      for {
        _ <- log.info(s"start $id")
        res <- send.toEveryone(id)
        _ <- log.info(s"Status of response: ${res.status} with data: ${res.data}")
      } yield res.data
    }
  }

  object `2` {
    trait Log[F[_]] {
      def info(message: String): F[Unit]
    }

    object Log {
      def apply[F[_]](implicit a: Log[F]): Log[F] = a
    }
    object Send {
      def apply[F[_]](implicit a: Send[F]): Send[F] = a
    }

    case class Response[A](status: Int, data: A)
    trait Send[F[_]] {
      def toEveryone(id: Long): F[Response[Boolean]]
    }

    def logAndSend[F[_]: Log: Send: Monad](id: Long): F[Boolean] =
      for {
        _ <- Log[F].info(s"Sending $id")
        res <- Send[F].toEveryone(id)
        _ <- Log[F].info(s"Sending info $res")
      } yield res.data
  }
}
