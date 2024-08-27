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

case object BetRoundEnd extends GameError {
  override def message: String = "You cant add bet, when game round is finished"
  override def errorId: String = "BET05"
}

case object AlreadyJoinedToTable extends GameError {
  override def message: String = "You already joined to the table"
  override def errorId: String = "TAB01"
}

case object TableNotExist extends GameError {
  override def message: String = "This table does not exist"
  override def errorId: String = "TAB02"
}

case object JoinToTheTable extends GameError {
  override def message: String = "Please join to the table and the you can make bets"
  override def errorId: String = "TAB03"
}

case object TooMuchPlayers extends GameError {
  override def message: String = "Too much players on this table"
  override def errorId: String = "TAB03"
}