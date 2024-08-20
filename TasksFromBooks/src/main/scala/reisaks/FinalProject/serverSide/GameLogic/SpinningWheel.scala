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

  private def program(): IO[Unit] = {
    def loop: IO[Unit] = for {
      _ <- IO(tableActor ! BetsStart)
      _ <- IO.sleep(10.seconds)  // I added little bit more time
      _ <- IO(tableActor ! BetsEnd)
      _ <- IO.sleep(2.seconds)
      _ <- IO(tableActor ! GameStart)
      _ <- IO.sleep(5.seconds)
      number <- generateRandomNumber()
      _ <- IO(tableActor ! GameResult(number))
      _ <- IO.sleep(3.seconds)
      _ <- IO(tableActor ! GameEnd)
      _ <- loop
    } yield ()
    loop
  }

  override def run: IO[Unit] = program()
}



