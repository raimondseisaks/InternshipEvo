package reisaks.FinalProject.ClientSide
import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest}
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future, Promise}
import scala.io.StdIn
import scala.util.Success

object WebSocketClient extends App {
  if (args.length != 2) {
    println("Usage: WebSocketClient <server-url> <unique-player-id>")
    System.exit(1)
  }

  val serverUrl = args(0)
  val playerId = args(1)

  implicit val system = ActorSystem("WebSocketClientSystem")
  implicit val materializer = ActorMaterializer()
  import system.dispatcher

  val webSocketFlow = Http().webSocketClientFlow(WebSocketRequest(s"$serverUrl/$playerId"))

  var continue = true

  val incoming: Sink[Message, Future[Done]] = Sink.foreach[Message] {
    case message: TextMessage.Strict =>
      if (message.text == "Close-Connection") {
        continue = false
        println("Click enter to close client app")
      }
      else if (message.text.matches("""\d+""")) {}
      else {
        println(s"Server: ${message.text}")
        System.out.flush()
      }
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
      println(s"Connected to spinning wheel game server as $playerId")
      println("Join to the available table to play the game")
    }
    else {
      println("Please choose another playerID (must me unique)")
      sys.exit(0)
    }
  }
  Await.result(connected, 5.seconds)

  while (continue) {
    val input = StdIn.readLine()
    input.split("\\s+") match {
      case Array("Exit-Server") => wsActor ! input
      case Array("Join-Table", tableName) => wsActor ! input
      case Array("Exit-Table") => wsActor ! input
      case Array("Add-Bet", betCode, amount) => wsActor ! input
      case Array("Show-Available-Tables") => wsActor ! input
      case _ if continue => println("Unknown command")
      case _ => println("Thanks for gaming!")
    }
  }

  // Ensure system termination
  closed.onComplete {
    case Success(_) =>
      println("WebSocket connection closed")
      system.terminate()
  }
}
