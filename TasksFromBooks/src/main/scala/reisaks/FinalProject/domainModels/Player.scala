package reisaks.FinalProject.domainModels

case class Player(playerId: String)

object onlinePlayerManager {
  private var existingPlayerIds: Set[String] = Set()           //State of online player's ids

  def createPlayer(id: String): Either[GameError, Player] = {
    if (existingPlayerIds.contains(id) || id.isEmpty) {
      Left(ExistingID)
    }
    else {
      val newPlayer = Player(id)
      existingPlayerIds += newPlayer.playerId
      Right(newPlayer)
    }
  }

  def removePlayer(player: Player): Unit = {
      existingPlayerIds -= player.playerId
    }
  }

