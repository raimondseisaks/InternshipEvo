package reisaks.FinalProject.DomainModels
import akka.actor.{ActorRef, ActorSystem, PoisonPill}
import cats.effect.{IO, Ref}
import reisaks.FinalProject.ServerSide.AkkaActors.PlayerActor
import reisaks.FinalProject.ServerSide.AkkaActors.TableActorMessages.{AddBetToTable, LeaveTable}

case class Player(playerId: String, actorRef: ActorRef)

trait OnlinePLayerManagerTrait {
  def playerIdsRef: IO[Ref[IO, Map[Player, Option[ActorRef]]]] = Ref.of[IO, Map[Player, Option[ActorRef]]](Map.empty)
  def createPlayer(id: String): IO[Either[GameError, Player]]
  def removePlayer(player: Player): IO[Unit]
  def isPlayerPlaying(player: Player): IO[Boolean]
  def addToTable(player: Player, actorRef: ActorRef): IO[Unit]
  def leftTable(player: Player): IO[Unit]
  def addBetToTable(player: Player, bet: Bet): IO[Unit]
}

class OnlinePlayerManager(system: ActorSystem, ref: Ref[IO, Map[Player, Option[ActorRef]]]) extends OnlinePLayerManagerTrait {

  override def createPlayer(id: String): IO[Either[GameError, Player]] = {
    ref.modify { existingPlayerIds =>
      if (existingPlayerIds.keys.exists(_.playerId == id) || id.isEmpty) {
        (existingPlayerIds, Left(ExistingID))
      } else {
        val newActor = system.actorOf(PlayerActor.props(id), s"playerActor-$id")
        val newPlayer = Player(id, newActor)
        val updatedIds = existingPlayerIds + (newPlayer -> None)
        (updatedIds, Right(newPlayer))
      }
    }
  }

  override def removePlayer(player: Player): IO[Unit] = {
    IO {
      player.actorRef ! PoisonPill
    } >> ref.update(existingPlayerIds => existingPlayerIds - player)
  }

  def isPlayerPlaying(player: Player): IO[Boolean] =
    ref.get.map { playerMap =>
      playerMap.get(player) match {
        case Some(None) => false
        case Some(_) => true
      }
    }

  def leftTable(player: Player): IO[Unit] = {
    ref.update { playersMap =>
      playersMap.get(player) match {
        case Some(Some(table)) =>
          table ! LeaveTable(player)
          playersMap + (player -> None)
      }
    }
  }

  override def addToTable(player: Player, actorRef: ActorRef): IO[Unit] = {
    ref.update { playersMap =>
      playersMap.get(player) match {
        case Some(None) =>
          val updated = playersMap + (player -> Some(actorRef))
          updated
      }
    }
  }

  override def addBetToTable(player: Player, bet: Bet): IO[Unit] =
    ref.get.flatMap( w => w.get(player) match {
      case Some(Some(table)) => IO{table ! AddBetToTable(player, bet)}
    })

}








