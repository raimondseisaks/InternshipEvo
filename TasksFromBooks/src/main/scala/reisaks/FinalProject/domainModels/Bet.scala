package reisaks.FinalProject.domainModels
import scala.util.Try

case class Bet(BET_ID: ID, BET_Code: Int, AMOUNT: BigDecimal)

object Bet {

  def create(betCode: String, amount: String): Either[GameError, Bet] = {
    for {
      code        <- parseCode(betCode)
      amount      <- parseAmount(amount)
      validAmount <- validateAmount(amount)
      validCode   <- validateCode(code)
    } yield Bet(new ID, validCode, validAmount)
  }

  private def parseCode(betCode: String): Either[GameError, Int] =
    Try(betCode.toInt).toOption.toRight(IncorrectBetCode)

  private def parseAmount(amount: String): Either[GameError, BigDecimal] =
    Try(BigDecimal(amount)).toOption.toRight(IncorrectBetAmountType)

  private def validateAmount(amount: BigDecimal): Either[GameError, BigDecimal] = {
    Either.cond(amount > 0, amount, IncorrectBetAmountInt)
  }

  private def validateCode(code: Int): Either[GameError, Int] = {
    Either.cond(code >= 1 && code <= 100, code, IncorrectBetCode)
  }
}


