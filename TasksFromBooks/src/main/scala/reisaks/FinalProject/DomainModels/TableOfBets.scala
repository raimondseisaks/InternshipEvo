package reisaks.FinalProject.DomainModels

case class TableOfBets(tableId: Id, playerBets: Map[Player, List[Bet]])  {

  def addPlayerBet(player: Player, bet: Bet): Either[GameError, TableOfBets] = {
    playerBets.get(player) match {

      case Some(existingBets) if existingBets.exists(_.betCode == bet.betCode) =>
        Left(ExistingBetCode)

      case Some(existingBets) =>
        Right(copy(playerBets = playerBets + (player -> (bet :: existingBets))))

      case _ =>
        Right(copy(playerBets = playerBets + (player -> List(bet))))
    }
  }


  def cleanTable(): TableOfBets = {
    copy(playerBets = playerBets.empty)
  }
}

object TableOfBets {
  def create: TableOfBets = TableOfBets(new Id, Map.empty)
}
