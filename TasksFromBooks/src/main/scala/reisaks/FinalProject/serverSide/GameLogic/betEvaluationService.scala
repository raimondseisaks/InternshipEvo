package reisaks.FinalProject.serverSide.GameLogic

import reisaks.FinalProject.domainModels.{Table, Player}

object betEvaluationService {
  def evaluateSum(player: Player, table: Table, winningNum: Int): Option[BigDecimal] =
    table.playerBets.get(player) match {
      case Some(bets) =>
        val sum = bets.foldLeft(BigDecimal(0)) { (acc, w) =>
          if (winningNum == w.betCode)
            acc + winningCoefficient(w.amount, winningNum)
          else acc
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
