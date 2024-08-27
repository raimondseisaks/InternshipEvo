package reisaks.FinalProject.DomainModels
import akka.util.Timeout
import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import cats.effect.unsafe.implicits.global
import reisaks.FinalProject.ServerSide.AkkaActors.TableActorRef._
import reisaks.FinalProject.ServerSide.AkkaActors.TableActorMessages._
import reisaks.FinalProject.ServerSide.AkkaActors.PlayerActorMessages._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import scala.util.Success

object TableManager {
  import AllTables._
  implicit val timeout: Timeout = 5.seconds
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  def joinTable(player: Player, tableName: String, playerManager: OnlinePLayerManagerTrait): Unit = {
    allTableRef.find(_.path.name == tableName) match {
      case Some(table) =>
        val isPlaying = playerManager.isPlayerPlaying(player).unsafeRunSync()

        if (isPlaying) {
          player.actorRef ! MessageToPlayer(AlreadyJoinedToTable.message)
        }
        else {
          table ! JoinTable(player, playerManager, table)
        }
      case None =>
        player.actorRef ! MessageToPlayer(TableNotExist.message)
    }
  }

  def exitTable(player: Player, playerManager: OnlinePLayerManagerTrait): Unit = {
    playerManager.leftTable(player).unsafeToFuture()
  }

  def showAvailableTables(player: Player): Unit =
    allTableRef.foreach {
      w =>
        val freePlace = (w ? AvailablePlaces).mapTo[Int]
        freePlace.onComplete {
          case Success(value) =>
            if (value > 0) player.actorRef ! MessageToPlayer(s"${w.path.name} has $value seats")
        }
    }

  def addBet(player: Player, playerManager: OnlinePLayerManagerTrait, bet: Bet): Unit = {
    val isPlaying = playerManager.isPlayerPlaying(player).unsafeToFuture()
    isPlaying.onComplete {
      case Success(true) => playerManager.addBetToTable(player, bet).unsafeToFuture()
      case Success(false) => player.actorRef ! MessageToPlayer(JoinToTheTable.message)
    }
  }

}

object AllTables {
  val system: ActorSystem = ActorSystem("MyActorSystem")
  val tableOneActor: ActorRef = system.actorOf(tableProps, "Table-1")
  val tableTwoActor: ActorRef = system.actorOf(tableProps, "Table-2")
  val tableThreeActor: ActorRef = system.actorOf(tableProps, "Table-3")
  val tableFourActor: ActorRef = system.actorOf(tableProps, "Table-4")
  val tableFiveActor: ActorRef = system.actorOf(tableProps, "Table-5")
  val tableSixActor: ActorRef = system.actorOf(tableProps, "Table-6")
  val tableSevenActor: ActorRef = system.actorOf(tableProps, "Table-7")
  val allTableRef: Set[ActorRef] = Set(
                                  tableOneActor,
                                  tableTwoActor,
                                  tableThreeActor,
                                  tableFourActor,
                                  tableFiveActor,
                                  tableSixActor,
                                  tableSevenActor)
}


