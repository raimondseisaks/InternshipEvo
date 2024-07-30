package reisaks.Bootcamp2023.iotf_practise

import cats.syntax.applicative._
import cats.Applicative
import cats.effect.std.Console

object ex1 {

  sealed trait State
  case object Open   extends State
  case object Closed extends State

  trait Metrics[F[_]] {
    def increase: F[Unit]
    def decrease: F[Unit]
    def gaugeTo(state: State): F[Unit]
  }
  object Metrics {
    /*
     * Task: implement, should do nothing
     */
    def empty[F[_]: Applicative]: Metrics[F] = new Metrics[F] {
      def increase: F[Unit]              = ().pure
      def decrease: F[Unit]              = ().pure
      def gaugeTo(state: State): F[Unit] = ().pure
    }

    /*
     * Task: implement, should print to console "increase", "decrease", and s"gauge: $state"
     */
    def console[F[_]: Console]: Metrics[F] = new Metrics[F] {
      def increase: F[Unit]              = Console[F].println("Increased")
      def decrease: F[Unit]              = Console[F].println("Decreased")
      def gaugeTo(state: State): F[Unit] = Console[F].println(s"gauge; $state")
    }
  }

}