package reisaks.FinalProject.ServerSide.WebSocket
import akka.NotUsed
import akka.http.scaladsl.model.StatusCodes
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import cats.effect.{IO, IOApp, Ref}
import cats.effect.unsafe.implicits.global
import reisaks.FinalProject.DomainModels._

import scala.io.StdIn
import akka.actor.ActorRef
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Sink, Source}
import reisaks.FinalProject.ServerSide.AkkaActors.PlayerActorMessages._
import reisaks.FinalProject.DomainModels.TableManager._

import scala.util.Success


object WebSocketServer extends IOApp.Simple {

  def run: IO[Unit] = {
    for {
      system <- IO(ActorSystem("MyActorSystem"))
      playerIdsRef <- Ref.of[IO, Map[Player, Option[ActorRef]]](Map.empty)
      playerManager = new OnlinePlayerManager(system, playerIdsRef)
      _ <- startServer(playerManager, system)
    } yield ()
  }

  private def startServer(playerManager: OnlinePLayerManagerTrait, system: ActorSystem): IO[Unit] = {

    IO {
      implicit val sys: ActorSystem = system
      implicit val materializer: ActorMaterializer = ActorMaterializer()

      val route = {
        pathPrefix(Segment) { playerId =>
          onSuccess(playerManager.createPlayer(playerId).unsafeToFuture()) {
            case Right(player) =>
              handleWebSocketMessages(webSocketFlow(player, playerManager))
            case Left(error) =>
              complete(StatusCodes.Forbidden, s"Failed to create player: $error")
          }
        }
      }

      val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

      println(s"Server is now online at http://localhost:8080\nPress RETURN to stop...")
      StdIn.readLine()
      bindingFuture
        .flatMap(_.unbind())
        .onComplete(_ => system.terminate())
    }
  }

  private def webSocketFlow(player: Player, playerManager: OnlinePLayerManagerTrait)
                           (implicit system: ActorSystem): Flow[Message, Message, Any] = {

    val sourceWithActorRef: Source[Message, ActorRef] =
      Source.actorRef[Message](bufferSize = 10, OverflowStrategy.fail)

    val (playerActorRef, source): (ActorRef, Source[Message, NotUsed]) = sourceWithActorRef.preMaterialize()

    player.actorRef ! RegisterWebSocket(playerActorRef)

    val incoming: Sink[Message, Any] =
      Flow[Message]
      .collect {
        case TextMessage.Strict(text) => text
      }
      .map { msg =>
        msg.split("\\s+") match {
          case Array("Join-Table", tableName) => joinTable(player, tableName, playerManager)
          case Array("Exit-Table") => exitTable(player, playerManager)
          case Array("Add-Bet", betCode, amount) =>
            Bet.create(betCode, amount) match {
              case Right(bet) => addBet(player, playerManager, bet)
              case Left(error) => player.actorRef ! MessageToPlayer(error.message)
            }
          case Array("Show-Available-Tables") => showAvailableTables(player)
          case Array("Exit-Server") =>
            playerManager.isPlayerPlaying(player).unsafeToFuture().onComplete {
              case Success(result) =>
                if (result) {
                  player.actorRef ! MessageToPlayer("Please leave the table")
                }
                else {
                  player.actorRef ! MessageToPlayer("Close-Connection")
                  playerManager.removePlayer(player).unsafeToFuture()
                }
            }
        }
      }.to(Sink.ignore)
    Flow.fromSinkAndSource(incoming, source)
  }

}


