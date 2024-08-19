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

  var roundState: gameState = betsStart
  var table: Table = Table.create
  var playingPlayers: Set[Player] = Set()

  def receive: Receive = {
    case JoinTable(player) =>
      if (playingPlayers.contains(player)) {
        sender() ! TextMessage("You already joined")
      }
      else {
        playingPlayers += player
        sender() ! TextMessage(s"You joined the table")

        if (playingPlayers.size == 1) {
          SpinningWheel.run.unsafeToFuture() // i will made that it will turns out when players.size < 1
        }
      }

    case BetsStart =>
      println("BetStart")
      roundState = betsStart

    case addBetToTable(player, bet) =>
      if (playingPlayers.contains(player)) {
        if (roundState == betsStart)
            table.addPlayerBet(player, bet) match {
              case Right(value) =>
                table = value
                sender() ! TextMessage(s"You bet on ${bet.betCode} with amount ${bet.amount}")
              case Left(error) => sender() ! TextMessage(error.message)
            }
        else sender() ! TextMessage(betRoundEnd.message)
        }
      else {
        sender() ! TextMessage("Please join the table!")
      }

    case BetsEnd =>
      roundState = betsEnd

    case GameStart =>
      roundState = gameStart

    case GameResult(winningNumber) =>
      playingPlayers.foreach { w =>
        val sum = evaluateSum(w, table, winningNumber)
        w.actorRef ! MessageToPlayer(s"You won $sum")
      }
      roundState = gameResults
      table = table.cleanTable()

    case GameEnd =>
      roundState = sessionEnd

    case LeaveTable(player) =>
      if (playingPlayers.contains(player)) {
        playingPlayers -= player
        sender() ! TextMessage("You left the table")
        }
      else {
        sender() ! TextMessage("You can't leave the table without logging in")
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



