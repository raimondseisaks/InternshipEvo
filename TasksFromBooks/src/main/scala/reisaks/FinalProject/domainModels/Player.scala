package reisaks.FinalProject.domainModels
import akka.actor.{ActorRef, ActorSystem, PoisonPill}
import cats.effect.{IO, Ref}
import reisaks.FinalProject.serverSide.AkkaActors.PlayerActor


case class Player(playerId: String, actorRef: ActorRef)
object OnlinePlayerManager {
 val playerIdsRef: IO[Ref[IO, Set[String]]] = Ref.of[IO, Set[String]](Set.empty)

  val system: ActorSystem = ActorSystem("MyActorSystem")

  def createPlayer(id: String): IO[Either[GameError, Player]] = {
    playerIdsRef.flatMap { ref =>
      ref.modify { existingPlayerIds =>
        println(existingPlayerIds)
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
  }

  def removePlayer(player: Player): IO[Unit] = {
    IO {
      player.actorRef ! PoisonPill
    } >> playerIdsRef.flatMap { ref =>
      ref.update(existingPlayerIds => existingPlayerIds - player.playerId)
    }
  }
}

