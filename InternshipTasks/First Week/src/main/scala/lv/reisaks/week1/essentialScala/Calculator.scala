sealed trait Calculation
final case class Success(result: Int) extends Calculation
final case class Failure(reason: String) extends Calculation

object Calculator {
  def +(calc: Calculation, operand: Int): Calculation =
    calc match {
      case Success(result) => Success(result + operand)
      case Failure(reason) => Failure(reason)
    }
  def -(calc: Calculation, operand: Int): Calculation =
    calc match {
      case Success(result) => Success(result - operand)
      case Failure(reason) => Failure(reason)
    }
  def /(calc: Calculation, operand: Int): Calculation =
    calc match {
      case Success(result) =>
        operand match {
          case 0 => Failure("Division by zero")
          case _ => Success(result / operand)
        }
      case Failure(reason) => Failure(reason)
    }
}