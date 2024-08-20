package reisaks.FinalProject.clientSide

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.io.StdIn

object WebSocketClient extends App {
  if (args.length != 2) {
    println("Usage: WebSocketClient <server-url> <player-id>")
    System.exit(1)
  }

  val serverUrl = args(0)
  val playerId = args(1)

  implicit val system = ActorSystem("WebSocketClientSystem")
  implicit val materializer = ActorMaterializer()
  import system.dispatcher

  val webSocketFlow = Http().webSocketClientFlow(WebSocketRequest(s"$serverUrl/$playerId"))

  val incoming: Sink[Message, Future[Done]] = Sink.foreach[Message] {
    case message: TextMessage.Strict =>
      println(s"Server: ${message.text}")
      System.out.flush()
  }

  val outgoing = Source.actorRef[String](bufferSize = 10, OverflowStrategy.fail)
    .map((msg: String) => TextMessage(msg))

  val ((wsActor, upgradeResponse), closed) =
    outgoing
      .viaMat(webSocketFlow)(Keep.both)
      .toMat(incoming)(Keep.both)
      .run()

  val connected = upgradeResponse.map { upgrade =>
    if (upgrade.response.status.isSuccess()) {
      println(s"Connected to WebSocket server as $playerId")
    }
    else {
      println("Please choose another playerID")
      sys.exit(0)
    }
  }
  Await.result(connected, 5.seconds)

  var continue = true
  while (continue) {
    val input = StdIn.readLine()

    input.split("\\s+") match {
      case Array("Exit-Server") =>
        continue = false
        wsActor ! input
      case Array("Join-Table") => wsActor ! input
      case Array("Exit-Table") => wsActor ! input
      case Array("Add-Bet", betCode, amount) => wsActor ! input
      case _ => println("Unknown command")
    }
  }

  // Close connection
  wsActor ! akka.actor.Status.Success("done")
  closed.onComplete(_ => system.terminate())
}
