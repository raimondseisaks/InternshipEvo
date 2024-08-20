package reisaks.FinalProject.serverSide.AkkaActors
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.ws.TextMessage
import cats.effect.unsafe.implicits.global
import reisaks.FinalProject.domainModels.{Bet, Player, Table}
import reisaks.FinalProject.domainModels._
import reisaks.FinalProject.serverSide.AkkaActors.PlayerActorMessages._
import reisaks.FinalProject.serverSide.GameLogic.betEvaluationService._
import reisaks.FinalProject.serverSide.GameLogic._

sealed trait gameState
case object betsStart extends gameState
case object betsEnd extends gameState
case object gameStart extends gameState
case object gameResults extends gameState
case object sessionEnd extends gameState

class tableActor extends Actor {
  import tableActorMessages._

  var roundState: gameState = betsStart   //Maybe it good idea to create state object
  var table: Table = Table.create
  var playingPlayers: Set[Player] = Set()

  SpinningWheel.run.unsafeToFuture() //Start Game service (it will works till server works)

  def receive: Receive = {
    case JoinTable(player) =>
      if (playingPlayers.contains(player)) {
        player.actorRef ! MessageToPlayer("You already joined to the table")
      }
      else {
        playingPlayers += player
        player.actorRef ! MessageToPlayer(s"You joined the table")
      }

    case BetsStart =>
      playingPlayers.foreach {
        player => player.actorRef ! MessageToPlayer("Round started. Place your bets!")
      }
      roundState = betsStart

    case addBetToTable(player, bet) =>
      if (playingPlayers.contains(player)) {
        if (roundState == betsStart)
            table.addPlayerBet(player, bet) match {
              case Right(value) =>
                table = value
                player.actorRef ! MessageToPlayer(s"You bet on ${bet.betCode} with amount ${bet.amount}")
              case Left(error) => player.actorRef ! MessageToPlayer(error.message)
            }
        else player.actorRef ! MessageToPlayer(betRoundEnd.message)
        }
      else {
        player.actorRef ! MessageToPlayer("Please join the table!")
      }

    case BetsEnd =>
      playingPlayers.foreach {
        player => player.actorRef ! MessageToPlayer("Betting has ended")
      }
      roundState = betsEnd

    case GameStart =>
      playingPlayers.foreach {
        player => player.actorRef ! MessageToPlayer("Game is started! Wheel is spinning.......")
      }
      roundState = gameStart

    case GameResult(winningNumber) =>
      playingPlayers.foreach { w =>
        val sum = evaluateSum(w, table, winningNumber)
        sum match {
          case Some(value) => w.actorRef ! MessageToPlayer(s"Winning number $winningNumber! You won $value")
          case None => w.actorRef ! MessageToPlayer(s"Winning number $winningNumber!")
        }
      }
      roundState = gameResults
      table = table.cleanTable()

    case GameEnd =>
      playingPlayers.foreach {
        player => player.actorRef ! MessageToPlayer("Round has ended")
      }
      roundState = sessionEnd

    case LeaveTable(player) =>
      if (playingPlayers.contains(player)) {
        playingPlayers -= player
        player.actorRef ! MessageToPlayer("You left the table")
        }
      else {
        player.actorRef ! MessageToPlayer("You can't leave the table without logging in")
      }
  }
}


object tableActorMessages {
  case object BetsStart
  case object BetsEnd
  case object GameStart
  case class GameResult(winningNumber: Int)
  case object GameEnd
  case class addBetToTable(player: Player, bet: Bet)
  case class JoinTable(player: Player)
  case class LeaveTable(player: Player)
}

object tableActorRef {
  val system: ActorSystem = ActorSystem("MyActorSystem")
  val tableActor: ActorRef = system.actorOf(Props[tableActor], "tableActor")
}



