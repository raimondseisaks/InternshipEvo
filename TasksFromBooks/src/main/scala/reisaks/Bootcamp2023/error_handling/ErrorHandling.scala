package reisaks.Bootcamp2023.error_handling
import cats.data.ValidatedNec
import cats.syntax.all._

object ErrorHandling extends App {

  // Exercise. Implement `parseIntOption` method.
  def parseIntOption(string: String): Option[Int] = string.toIntOption

  // Exercise. Implement `parseIntEither` method, returning the parsed integer as `Right` upon success and
  // "{{string}} does not contain an integer" as `Left` upon failure.
  def parseIntEither(string: String): Either[String, Int] = {
    val num = parseIntOption(string)
    num match {
      case None => Left(s"$string does not contain an integer")
      case Some(num) => Right(num)
    }
  }

  sealed trait TransferError
  object TransferError {

    /** Returned when amount to credit is negative. */
    final case object NegativeAmount extends TransferError

    /** Returned when amount to credit is zero. */
    final case object ZeroAmount extends TransferError

    /** Returned when amount to credit is equal or greater than 1 000 000. */
    final case object AmountIsTooLarge extends TransferError

    /** Returned when amount to credit is within the valid range, but has more than 2 decimal places. */
    final case object TooManyDecimals extends TransferError
  }
  // Exercise. Implement `credit` method, returning `Unit` as `Right` upon success and the appropriate
  // `TransferError` as `Left` upon failure.
  def credit(amount: BigDecimal): Either[TransferError, Unit] =
    amount match {
      case _ if amount < 0       => Left(TransferError.NegativeAmount)
      case _ if amount == 0      => Left(TransferError.ZeroAmount)
      case _ if amount > 1000000 => Left(TransferError.AmountIsTooLarge)
      case _ if amount.scale > 2 => Left(TransferError.TooManyDecimals)
      case _                     => Right()
    }



  final case class Username(value: String) extends AnyVal
  final case class Age(value: Int)         extends AnyVal
  final case class Student(username: Username, age: Age)

  sealed trait ValidationError
  object ValidationError {
    final case object UsernameLengthIsInvalid      extends ValidationError {
      override def toString: String = "Username must be between 3 and 30 characters"
    }
    final case object UsernameHasSpecialCharacters extends ValidationError {
      override def toString: String = "Username cannot contain special characters"
    }
    final case object AgeIsNotNumeric              extends ValidationError {
      override def toString: String = "Age must be a number"
    }
    final case object AgeIsOutOfBounds             extends ValidationError {
      override def toString: String = "Student must be of age 18 to 75"
    }
  }

  object StudentValidator {

    import ValidationError._

    type AllErrorsOr[A] = ValidatedNec[ValidationError, A]

    private def validateUsername(username: String): AllErrorsOr[Username] = {

      def validateUsernameLength: AllErrorsOr[String] =
        if (username.length >= 3 && username.length <= 30) username.validNec
        else UsernameLengthIsInvalid.invalidNec

      def validateUsernameContents: AllErrorsOr[String] =
        if (username.matches("^[a-zA-Z0-9]+$")) username.validNec
        else UsernameHasSpecialCharacters.invalidNec

      validateUsernameLength.productR(validateUsernameContents).map(Username)
    }

    // Exercise. Implement `validateAge` method, so that it returns `AgeIsNotNumeric` if the age string is not
    // a number and `AgeIsOutOfBounds` if the age is not between 18 and 75. Otherwise the age should be
    // considered valid and returned inside `AllErrorsOr`.
    private def validateAge(age: String): AllErrorsOr[Age] = {
      def isNumber: AllErrorsOr[Int] =
        age.toIntOption match {
          case Some(value) => value.validNec
          case None => AgeIsNotNumeric.invalidNec
        }

      def isInRange(ageValue: Int): AllErrorsOr[Int] =
        if (ageValue >= 18 && ageValue <= 75) ageValue.validNec
        else AgeIsOutOfBounds.invalidNec

      isNumber.andThen(isInRange).map(Age)
    }


    def validate(username: String, age: String): AllErrorsOr[Student] =
      (validateUsername(username), validateAge(age)).mapN(Student)
  }
}
