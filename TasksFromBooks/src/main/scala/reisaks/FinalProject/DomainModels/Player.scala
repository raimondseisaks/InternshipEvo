package reisaks.FinalProject.DomainModels
import akka.actor.{ActorRef, ActorSystem, PoisonPill}
import cats.effect.{IO, Ref}
import reisaks.FinalProject.ServerSide.AkkaActors.PlayerActor


case class Player(playerId: String, actorRef: ActorRef)

trait OnlinePLayerManagerTrait {
  val playerIdsRef: IO[Ref[IO, Set[String]]] = Ref.of[IO, Set[String]](Set.empty)
  def createPlayer(id: String): IO[Either[GameError, Player]]
  def removePlayer(player: Player): IO[Unit]
}

class OnlinePlayerManager(system: ActorSystem, ref: Ref[IO, Set[String]]) extends OnlinePLayerManagerTrait {

  override def createPlayer(id: String): IO[Either[GameError, Player]] = {
    ref.modify { existingPlayerIds =>
      if (existingPlayerIds.contains(id) || id.isEmpty) {
        (existingPlayerIds, Left(ExistingID))
      } else {
        val newActor = system.actorOf(PlayerActor.props(id), s"playerActor-$id")
        val newPlayer = Player(id, newActor)
        val updatedIds = existingPlayerIds + newPlayer.playerId
        (updatedIds, Right(newPlayer))
      }
    }
  }

  override def removePlayer(player: Player): IO[Unit] = {
    IO {
      player.actorRef ! PoisonPill
    } >> ref.update(existingPlayerIds => existingPlayerIds - player.playerId)
  }
}






