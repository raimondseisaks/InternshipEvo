package reisaks.Bootcamp2023.testing2.hal9000

object hal9000 {

  private[hal9000] def twice(x: Int): Int = x * 2

  def letAustronautIn(): Unit = {
    throw new RuntimeException("I'm sorry Dave, I'm afraid I can't do that")
  }

  val register1: Int = 0
  val register2: Int = 1

}
