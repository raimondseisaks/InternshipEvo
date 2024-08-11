package reisaks.FinalProject.domainModels

case class Player(PLAYER_ID: String)

object OnlinePlayerManager {
  private var existingPlayerIds: Set[String] = Set()           //State of online player's ids

  def createPlayer(id: String): Either[GameError, Player] = {
    if (existingPlayerIds.contains(id) && id.isEmpty) {
      Left(ExistingID)
    }
    else {
      val newPlayer = Player(id)
      existingPlayerIds += newPlayer.PLAYER_ID
      Right(newPlayer)
    }
  }

  def removePlayer(player: Player): Unit = {
      existingPlayerIds -= player.PLAYER_ID
    }
  }

