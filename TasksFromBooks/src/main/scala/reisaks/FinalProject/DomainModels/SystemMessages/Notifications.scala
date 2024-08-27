package reisaks.FinalProject.DomainModels.SystemMessages

sealed trait Notifications {    // I will add more system notifications (this is created for university purposes (documentation))
  def message: String
  def errorId: String
}

case object RoundStarted extends Notifications {
  override def message: String = "Round started. Place your bets!"
  override def errorId: String = "NF01"
}

case object SuccessfullyJoinedToTable extends Notifications {
  override def message: String = "Round started. Place your bets!"
  override def errorId: String = "NF02"
}

case object BetHasEnded extends Notifications {
  override def message: String = "Betting has ended!"
  override def errorId: String = "NF03"
}

case object GameIsStarted extends Notifications {
  override def message: String = "Game is started! Wheel is spinning......."
  override def errorId: String = "NF04"
}
