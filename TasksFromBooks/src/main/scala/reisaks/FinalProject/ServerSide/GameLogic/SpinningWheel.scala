package reisaks.FinalProject.ServerSide.GameLogic

import akka.actor.ActorRef
import cats.effect.IO
import reisaks.FinalProject.ServerSide.AkkaActors.TableActorMessages._
import scala.concurrent.duration._
import scala.util.Random

object SpinningWheel {

  private def generateRandomNumber(): IO[Int] = IO {
    Random.nextInt(101)
  }

  def program(tableRef: ActorRef): IO[Unit] = {
    def loop: IO[Unit] = for {
      _ <- IO(tableRef ! BetsStart)
      _ <- IO.sleep(10.seconds)
      _ <- IO(tableRef ! BetsEnd)
      _ <- IO.sleep(2.seconds)
      _ <- IO(tableRef ! GameStart)
      _ <- IO.sleep(5.seconds)
      number <- generateRandomNumber()
      _ <- IO(tableRef ! GameResult(number))
      _ <- IO.sleep(3.seconds)
      _ <- IO(tableRef ! GameEnd)
    } yield ()
    loop
  }
}




