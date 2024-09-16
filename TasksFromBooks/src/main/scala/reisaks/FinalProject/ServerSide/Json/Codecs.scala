package reisaks.FinalProject.ServerSide.Json

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import reisaks.FinalProject.ServerSide.AkkaActors.{BetResult, GameState}
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.{deriveConfiguredDecoder, deriveConfiguredEncoder}


//Very simple usage of cicre semi-auto JsonCodecs
case class GameStarted(tableName: String, roundId:String, state: String)
object GameStarted {
  implicit val encoder: Encoder[GameStarted] = deriveEncoder[GameStarted]
  implicit val decoder: Decoder[GameStarted] = deriveDecoder[GameStarted]
}

object BetResult {   //Encoder and decoder for sealed trait of BetResult
  implicit val config: Configuration = Configuration.default
  implicit val encoderBetResult: Encoder[BetResult] = deriveConfiguredEncoder[BetResult]
  implicit val decoderBetResult: Decoder[BetResult] = deriveConfiguredDecoder[BetResult]
}


case class GameEnded(tableName: String, roundId: String, state: String, betsList: Map[String, BetResult])
object GameEnded {
  import BetResult._
  implicit val encoderEnd: Encoder[GameEnded] = deriveEncoder[GameEnded]
  implicit val decoderEnd: Decoder[GameEnded] = deriveDecoder[GameEnded]
}
