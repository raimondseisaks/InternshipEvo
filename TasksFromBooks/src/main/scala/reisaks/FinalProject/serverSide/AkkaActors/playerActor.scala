package reisaks.FinalProject.serverSide.AkkaActors

import akka.actor.{Actor, Props}
import akka.http.scaladsl.model.ws.TextMessage

class PlayerActor(playerId: String) extends Actor {
  import PlayerActorMessages._

  override def receive: Receive = {
    case MessageToPlayer(text) => TextMessage(s"Player $playerId received message: $text")
  }
}
object PlayerActorMessages {
  case class MessageToPlayer(text: String)
}

object PlayerActor {
  def props(playerId: String): Props = Props(new PlayerActor(playerId))
}



