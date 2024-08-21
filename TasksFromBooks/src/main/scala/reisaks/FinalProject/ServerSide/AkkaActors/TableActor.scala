package reisaks.FinalProject.ServerSide.AkkaActors
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import cats.effect.unsafe.implicits.global
import reisaks.FinalProject.DomainModels.{Bet, Player, Table}
import reisaks.FinalProject.DomainModels._
import reisaks.FinalProject.ServerSide.AkkaActors.PlayerActorMessages._
import reisaks.FinalProject.ServerSide.GameLogic.BetEvaluationService._
import reisaks.FinalProject.ServerSide.GameLogic._

sealed trait GameState
case object BetsStartState extends GameState
case object BetsEndState extends GameState
case object GameStartState extends GameState
case object GameResultsState extends GameState
case object SessionEndState extends GameState

class TableActor extends Actor {
  import tableActorMessages._

  var roundState: GameState = BetsStartState   //Maybe it good idea to create state object
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
      roundState = BetsStartState

    case AddBetToTable(player, bet) =>
      if (playingPlayers.contains(player)) {
        if (roundState == BetsStartState)
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
      roundState = BetsEndState

    case GameStart =>
      playingPlayers.foreach {
        player => player.actorRef ! MessageToPlayer("Game is started! Wheel is spinning.......")
      }
      roundState = GameStartState

    case GameResult(winningNumber) =>
      playingPlayers.foreach { w =>
        val sum = evaluateSum(w, table, winningNumber)
        sum match {
          case Some(value) => w.actorRef ! MessageToPlayer(s"Winning number $winningNumber! You won $value")
          case None => w.actorRef ! MessageToPlayer(s"Winning number $winningNumber!")
        }
      }
      roundState = GameResultsState
      table = table.cleanTable()

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
  }
}


object TableActorMessages {
  case object BetsStart
  case object BetsEnd
  case object GameStart
  case class GameResult(winningNumber: Int)
  case object GameEnd
  case class AddBetToTable(player: Player, bet: Bet)
  case class JoinTable(player: Player)
  case class LeaveTable(player: Player)
}

object TableActorRef {
  val system: ActorSystem = ActorSystem("MyActorSystem")
  val tableActor: ActorRef = system.actorOf(Props[TableActor], "tableActor")
}



