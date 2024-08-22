package reisaks.FinalProject.DomainModels
import scala.util.Try

case class Bet(betId: Id, betCode: Int, amount: BigDecimal)

object Bet {

  def create(betCode: String, amount: String): Either[GameError, Bet] = {
    for {
      code        <- parseCode(betCode)
      amount      <- parseAmount(amount)
      validAmount <- validateAmount(amount)
      validCode   <- validateCode(code)
    } yield Bet(new Id, validCode, validAmount)
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


