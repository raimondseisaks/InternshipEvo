package reisaks.FinalProject.domainModels

case class Table(TABLE_ID: ID, playerBets: Map[Player, Bet]) {

  def addPlayerBet(player: Player, bet: Bet): Either[GameError, Table] = {
    playerBets.get(player) match {
      case Some(existingBet) if existingBet.BET_Code == bet.BET_Code =>
        Left(ExistingBetCode)

      case _ =>
        Right(copy(playerBets = playerBets + (player -> bet)))
    }
  }
}

object Table {
  def create: Table = Table(new ID, Map.empty)
}