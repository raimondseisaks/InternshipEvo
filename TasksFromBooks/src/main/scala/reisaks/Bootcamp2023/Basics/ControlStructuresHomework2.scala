package reisaks.Bootcamp2023.Basics

import scala.io.Source

object ControlStructuresHomework2 {
  sealed trait Command
  object Command {
    final case class Divide(dividend: Double, divisor: Double) extends Command
    final case class Sum(numbers: List[Double])                extends Command
    final case class Average(numbers: List[Double])            extends Command
    final case class Min(numbers: List[Double])                extends Command
    final case class Max(numbers: List[Double])                extends Command
  }

  final case class ErrorMessage(value: String)

  sealed trait Result
  final case class Error(value: String) extends Result
  final case class Success(d: Double) extends Result
  // Function to parse input command string into Command object
  def parseCommand(x: String): Either[ErrorMessage, Command] = {   //There is possible exception if user inputs word instead numbers
    val commands = x.split(" ").toList
    def toDoubleList(strings: List[String]): Option[List[Double]] =
      strings.foldRight(Option(List.empty[Double])) { (str, acc) =>
        for {
          xs <- acc
          x <- str.toDoubleOption
        } yield x :: xs
      }

    commands match {
      case "divide" :: dividend :: divisor :: Nil =>
        (dividend.toDoubleOption, divisor.toDoubleOption) match {
          case (Some(d1), Some(d2)) => Right(Command.Divide(d1, d2))
          case _ => Left(ErrorMessage("Invalid numbers for divide command"))
        }
      case "sum" :: tail =>
        toDoubleList(tail) match {
          case Some(nums) => Right(Command.Sum(nums))
          case None => Left(ErrorMessage("Invalid numbers for sum command"))
        }
      case "average" :: tail =>
        toDoubleList(tail) match {
          case Some(nums) => Right(Command.Average(nums))
          case None => Left(ErrorMessage("Invalid numbers for average command"))
        }
      case "min" :: tail =>
        toDoubleList(tail) match {
          case Some(nums) => Right(Command.Min(nums))
          case None => Left(ErrorMessage("Invalid numbers for min command"))
        }
      case "max" :: tail =>
        toDoubleList(tail) match {
          case Some(nums) => Right(Command.Max(nums))
          case None => Left(ErrorMessage("Invalid numbers for max command"))
        }
      case _ => Left(ErrorMessage("Invalid Command"))
    }
  }

  // Function to calculate result based on Command
  def calculate(command: Command): Either[ErrorMessage, Result] = {
    command match {
      case Command.Divide(_, 0) => Left(ErrorMessage("Division by zero"))
      case Command.Divide(dividend, divisor) => Right(Success(dividend / divisor))
      case Command.Sum(numbers) => if (numbers.isEmpty) Left(ErrorMessage("No input numbers")) else Right(Success(numbers.sum))
      case Command.Average(numbers) => if (numbers.isEmpty) Left(ErrorMessage("No input numbers")) else Right(Success(numbers.sum / numbers.length))
      case Command.Min(numbers) => if (numbers.isEmpty) Left(ErrorMessage("No input numbers")) else Right(Success(numbers.min))
      case Command.Max(numbers) => if (numbers.isEmpty) Left(ErrorMessage("No input numbers")) else Right(Success(numbers.max))
    }
  }

  // Function to render Result as String
  def renderResult(result: Result): String = {
    result match {
      case Success(value) => f"$value%.2f"
      case Error(message) => s"Error: $message"   //For further developments
    }
  }

  // Processing function
  def process(x: String): String = {
    parseCommand(x) match {
      case Right(command) =>
        calculate(command) match {
          case Right(result) => renderResult(result)
          case Left(error) => s"Error: ${error.value}"
        }
      case Left(error) => s"Error: ${error.value}"
    }
  }

  // Main method to read from stdin, process each line, and print results map process foreach println
  def main(args: Array[String]): Unit = Source.stdin.getLines() map process foreach println

}
