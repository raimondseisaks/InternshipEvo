package reisaks.FinalProject.ServerSide.GameLogic

import reisaks.FinalProject.DomainModels.{TableOfBets, Player}

object BetEvaluationService {
  def evaluateSum(player: Player, tableOfBets: TableOfBets, winningNum: Int): Option[BigDecimal] =
    tableOfBets.playerBets.get(player) match {
      case Some(bets) =>
        val sum = bets.foldLeft(BigDecimal(0)) { (acc, w) =>
          if (winningNum == w.betCode)
            acc + winningCoefficient(w.amount, winningNum)
          else acc - w.amount
        }
        Some(sum)
      case _ => None
    }

  private def winningCoefficient(amount: BigDecimal, winNum: Int): BigDecimal = {
    if (winNum == 100) amount * 50
    else if (winNum % 2 == 0) amount * 3
    else amount * 2
  }
}
