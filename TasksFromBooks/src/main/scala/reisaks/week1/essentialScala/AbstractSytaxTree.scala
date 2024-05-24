sealed trait Expression {
  def eval : Calculation =
    this match {

      case Addition(left, right) =>
        left.eval match {
          case Failure(reason) => Failure(reason)
          case Success(num1) =>
            right.eval match {
              case Failure(reason) => Failure(reason)
              case Success(num2) => Success(num1 + num2)
            }
        }

      case Subtraction(left, right) =>
        left.eval match {
          case Failure(reason) => Failure(reason)
          case Success(num1) =>
            right.eval match {
              case Failure(reason) => Failure(reason)
              case Success(num2) => Success(num1 - num2)
            }
        }

      case Division(left, right) =>
        left.eval match {
          case Failure(reason) => Failure(reason)
          case Success(num1) =>
            right.eval match {
              case Failure(reason) => Failure(reason)
              case Success(num2) =>
                if (num2 == 0) Failure("Division by 0")
                else Success(num1 / num2)
            }
        }
      case SquareRoot(exp) =>
        exp.eval match {
          case Failure(reason) => Failure(reason)
          case Success(val1) =>
            if (val1 < 0) Failure("Square root of negative number")
            else Success(math.sqrt(val1))
        }

      case Number(value) => Success(value)
    }

}

final case class Addition(left : Expression, right: Expression) extends Expression
final case class Subtraction(left: Expression, right: Expression) extends Expression
final case class Number(value : Double) extends Expression
final case class Division(left: Expression, right: Expression) extends Expression
final case class SquareRoot(value: Expression) extends Expression

//used in other files
sealed trait Calculation
final case class Success(result: Double) extends Calculation
final case class Failure(reason: String) extends Calculation