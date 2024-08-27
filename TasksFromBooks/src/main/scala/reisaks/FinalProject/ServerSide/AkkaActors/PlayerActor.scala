package reisaks.FinalProject.ServerSide.AkkaActors
import akka.actor.{Actor, ActorRef, Props}
import akka.http.scaladsl.model.ws.TextMessage


class PlayerActor(playerId: String) extends Actor {
  import PlayerActorMessages._

  //Register WebSocketRef (for sending individual messages)//
  def initWebRef: Receive = {
    case RegisterWebSocket(ref) =>
      context.become(regWebRef(ref))
  }

  // Behavior when WebSocket is registered
  def regWebRef(webSocketRef: ActorRef): Receive = {
    case MessageToPlayer(text) =>
      webSocketRef ! TextMessage(s"$text")
  }

  override def receive: Receive = initWebRef
}

object PlayerActorMessages {
  case class MessageToPlayer(text: String)
  case class RegisterWebSocket(ref: ActorRef)
}

object PlayerActor {
  def props(playerId: String): Props = Props(new PlayerActor(playerId))
}



