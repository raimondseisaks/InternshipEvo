package reisaks.FinalProject.serverSide.WebSocket
import akka.NotUsed
import akka.http.scaladsl.model.StatusCodes
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.remote.transport.ActorTransportAdapter.AskTimeout
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import cats.effect.{IO, IOApp, Ref}
import cats.effect.unsafe.implicits.global
import reisaks.FinalProject.domainModels._
import scala.io.StdIn
import reisaks.FinalProject.serverSide.AkkaActors.tableActorRef.tableActor
import reisaks.FinalProject.serverSide.AkkaActors.tableActorMessages._
import akka.actor.ActorRef
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Sink, Source}
import reisaks.FinalProject.serverSide.AkkaActors.PlayerActorMessages._


object WebSocketServer extends IOApp.Simple {

  def run: IO[Unit] = {
    for {
      system <- IO(ActorSystem("MyActorSystem"))
      playerIdsRef <- Ref.of[IO, Set[String]](Set.empty)
      playerManager = new OnlinePlayerManager(system, playerIdsRef)
      _ <- startServer(playerManager, system)
    } yield ()
  }

  private def startServer(playerManager: OnlinePLayerManagerTrait, system: ActorSystem): IO[Unit] = {
    IO {
      implicit val sys: ActorSystem = system
      implicit val materializer: ActorMaterializer = ActorMaterializer()
      import system.dispatcher

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
          case Array("Join-Table") => tableActor ? JoinTable(player)
          case Array("Exit-Table") => tableActor ? LeaveTable(player)
          case Array("Add-Bet", betCode, amount) =>
            Bet.create(betCode, amount) match {
              case Right(bet) =>
                tableActor ? addBetToTable(player, bet)
              case Left(error) => player.actorRef ? MessageToPlayer(error.message)
            }
          case Array("Exit-Server") =>
            player.actorRef ? MessageToPlayer("You disconnected form server")
            playerManager.removePlayer(player).unsafeToFuture()
        }
      }
      .to(Sink.ignore)

    Flow.fromSinkAndSource(incoming, source)
  }

}


