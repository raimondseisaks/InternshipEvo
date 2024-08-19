package reisaks.FinalProject.serverSide.WebSocket
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest}
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.remote.transport.ActorTransportAdapter.AskTimeout
import akka.stream.scaladsl.Flow
import akka.stream.ActorMaterializer
import cats.effect.{IO, IOApp}
import cats.effect.unsafe.implicits.global
import reisaks.FinalProject.domainModels.OnlinePlayerManager.{createPlayer, removePlayer}
import reisaks.FinalProject.domainModels._
import reisaks.FinalProject.serverSide.AkkaActors.tableActorMessages._
import reisaks.FinalProject.serverSide.AkkaActors.tableActorRef._

import scala.concurrent.Future
import scala.io.StdIn

object WebSocketServer extends IOApp.Simple {

  def run: IO[Unit] = {
    for {
      _ <- IO {
        implicit val system = ActorSystem()
        implicit val materializer = ActorMaterializer()
        import system.dispatcher

        val route =
          pathPrefix(Segment) { playerId =>
            onSuccess(createPlayer(playerId).unsafeToFuture()) {
              case Right(player) =>
                handleWebSocketMessages(webSocketFlow(player))
              case Left(error) =>
                complete(s"Failed to create player: $error")
            }
          }

        val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

        println(s"Server is now online at http://localhost:8080\nPress RETURN to stop...")
        StdIn.readLine()
        bindingFuture
          .flatMap(_.unbind())
          .onComplete(_ => system.terminate())
      }
    } yield ()
  }

  def webSocketFlow(player: Player)(implicit system: ActorSystem): Flow[Message, Message, Any] = {
    Flow[Message]
      .collect {
        case TextMessage.Strict(text) => text
      }
      .mapAsync(1) { msg =>
        msg.split("\\s+") match {
          case Array("Join-Table") => (tableActor ? JoinTable(player)).mapTo[TextMessage]
          case Array("Exit-Table") => (tableActor ? LeaveTable(player)).mapTo[TextMessage]
          case Array("Add-Bet", betCode, amount) =>
            Bet.create(betCode, amount) match {
              case Right(bet) => (tableActor ? addBetToTable(player, bet)).mapTo[TextMessage]

              case Left(error) => Future.successful(TextMessage(error.message))
          }
          case Array("Exit-Server") => removePlayer(player)
          Future.successful(TextMessage("You disconnected from server!"))
        }
      }
  }
}

