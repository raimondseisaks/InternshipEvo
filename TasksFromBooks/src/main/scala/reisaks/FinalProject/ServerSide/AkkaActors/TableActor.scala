package reisaks.FinalProject.ServerSide.AkkaActors
import akka.actor.{Actor, ActorRef, Props}
import cats.effect.unsafe.implicits.global
import reisaks.FinalProject.DomainModels.{Bet, Player, TableOfBets}
import reisaks.FinalProject.DomainModels._
import reisaks.FinalProject.ServerSide.AkkaActors.PlayerActorMessages._
import reisaks.FinalProject.ServerSide.GameLogic.BetEvaluationService._
import reisaks.FinalProject.DomainModels.OnlinePLayerManagerTrait
import reisaks.FinalProject.ServerSide.GameLogic.SpinningWheel
import reisaks.FinalProject.DomainModels.SystemMessages._

sealed trait GameState
case object BetsStartState extends GameState
case object BetsEndState extends GameState
case object GameStartState extends GameState
case object GameResultsState extends GameState
case object SessionEndState extends GameState

class TableActor extends Actor {
  import TableActorMessages._
  SpinningWheel.program(self).unsafeToFuture()

  var roundState: GameState = BetsStartState
  var tableOfBets: TableOfBets = TableOfBets.create
  var playingPlayers: Set[Player] = Set()

  def receive: Receive = {
    case JoinTable(player, playerManager, actorRef) =>
      if (playingPlayers.contains(player)) {
        player.actorRef ! MessageToPlayer(AlreadyJoinedToTable.message)
      }
      else if (playingPlayers.size >= 10) {
        player.actorRef ! MessageToPlayer(TooMuchPlayers.message)
      }
      else {
        playingPlayers += player
        playerManager.addToTable(player, actorRef).unsafeToFuture()
        player.actorRef ! MessageToPlayer(SuccessfullyJoinedToTable.message)
      }

    case BetsStart =>
      playingPlayers.foreach {
        player => player.actorRef ! MessageToPlayer(RoundStarted.message)
      }
      roundState = BetsStartState

    case AddBetToTable(player, bet) =>
      if (playingPlayers.contains(player)) {
        if (roundState == BetsStartState)
            tableOfBets.addPlayerBet(player, bet) match {
              case Right(value) =>
                tableOfBets = value
                player.actorRef ! MessageToPlayer(s"You bet on ${bet.betCode} with amount ${bet.amount}")
              case Left(error) => player.actorRef ! MessageToPlayer(error.message)
            }
        else player.actorRef ! MessageToPlayer(BetRoundEnd.message)
        }
      else {
        player.actorRef ! MessageToPlayer("Please join the table!")
      }

    case BetsEnd =>
      playingPlayers.foreach {
        player => player.actorRef ! MessageToPlayer(BetHasEnded.message)
      }
      roundState = BetsEndState

    case GameStart =>
      playingPlayers.foreach {
        player => player.actorRef ! MessageToPlayer(GameIsStarted.message)
      }
      roundState = GameStartState

    case GameResult(winningNumber) =>
      playingPlayers.foreach { w =>
        val sum = evaluateSum(w, tableOfBets, winningNumber)
        sum match {
          case Some(value) => w.actorRef ! MessageToPlayer(s"Winning number $winningNumber! You won $value")
          case None => w.actorRef ! MessageToPlayer(s"Winning number $winningNumber!")
        }
      }
      roundState = GameResultsState
      tableOfBets = tableOfBets.cleanTable()

    case GameEnd =>
      playingPlayers.foreach {
        player => player.actorRef ! MessageToPlayer("Round has ended")
      }
      roundState = SessionEndState

    case LeaveTable(player) =>
      if (playingPlayers.contains(player)) {
        playingPlayers -= player
        player.actorRef ! MessageToPlayer("You left the table")
        }
      else {
        player.actorRef ! MessageToPlayer("You can't leave the table without logging in")
      }

    case AvailablePlaces =>
      sender() ! (10 - playingPlayers.size)
  }
}

object TableActorMessages {
  case object BetsStart
  case object BetsEnd
  case object GameStart
  case class GameResult(winningNumber: Int)
  case object GameEnd
  case class AddBetToTable(player: Player, bet: Bet)
  case class JoinTable(player: Player, playerManager: OnlinePLayerManagerTrait, actorRef: ActorRef)
  case class LeaveTable(player: Player)
  case object AvailablePlaces

}

object TableActorRef {
  def tableProps: Props = Props(new TableActor)
}



