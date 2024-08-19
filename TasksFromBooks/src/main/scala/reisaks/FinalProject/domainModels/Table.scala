package reisaks.FinalProject.domainModels

case class Table(tableId: Id, playerBets: Map[Player, List[Bet]])  {

  def addPlayerBet(player: Player, bet: Bet): Either[GameError, Table] = {
    playerBets.get(player) match {

      case Some(existingBets) if existingBets.exists(_.betCode == bet.betCode) =>
        Left(ExistingBetCode)

      case Some(existingBets) =>
        Right(copy(playerBets = playerBets + (player -> (bet :: existingBets))))

      case _ =>
        Right(copy(playerBets = playerBets + (player -> List(bet))))
    }
  }


  def cleanTable(): Table = {
    copy(playerBets = playerBets.empty)
  }
}

object Table {
  def create: Table = Table(new Id, Map.empty)
}
