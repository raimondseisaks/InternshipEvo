package reisaks.FinalProject.DomainModels

sealed trait GameError {
 def message: String
  def errorId: String
}

case object IncorrectBetAmountInt extends GameError {
  override def message: String = "Bet amount must be greater than 0"
  override def errorId: String = "BET01"
}

case object IncorrectBetAmountType extends GameError {
  override def message: String = "Bet amount must be integer"
  override def errorId: String = "BET02"
}

case object IncorrectBetCode extends GameError {
  override def message: String = "Bet code must be number from 0 to 100"
  override def errorId: String = "BET03"
}

case object ExistingID extends GameError {
  override def message: String = "This player id already exists"
  override def errorId: String = "PL01"
}

case object ExistingBetCode extends GameError {
  override def message: String = "Bet with this code already exists"
  override def errorId: String = "BET04"
}

case object betRoundEnd extends GameError {
  override def message: String = "You cant add bet, when game round is finished"
  override def errorId: String = "BET05"
}