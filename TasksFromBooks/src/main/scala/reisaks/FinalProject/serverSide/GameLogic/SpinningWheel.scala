package reisaks.FinalProject.serverSide.GameLogic
import cats.effect.{IO, IOApp}
import reisaks.FinalProject.serverSide.AkkaActors.tableActorRef._
import reisaks.FinalProject.serverSide.AkkaActors.tableActorMessages._

import scala.concurrent.duration._
import scala.util.Random

object SpinningWheel extends IOApp.Simple {

  private def generateRandomNumber(): IO[Int] = IO {
    Random.nextInt(101)
  }

  def program(): IO[Unit] = {
    def loop: IO[Unit] = for {
      _ <- IO(tableActor ! BetsStart)
      _ <- IO.sleep(5.seconds)
      _ <- IO(tableActor ! BetsEnd)
      _ <- IO.sleep(2.seconds)
      _ <- IO(tableActor ! GameStart)
      _ <- IO.sleep(5.seconds)
      number <- generateRandomNumber()
      _ <- IO(tableActor ! GameResult(number))
      _ <- IO.sleep(3.seconds)
      _ <- loop
    } yield ()
    loop
  }

  override def run: IO[Unit] = {
    for {
      _ <- program()
    } yield ()
  }
}


