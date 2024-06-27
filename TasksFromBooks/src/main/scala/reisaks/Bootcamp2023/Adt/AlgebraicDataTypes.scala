package reisaks.Bootcamp2023.Adt

object AlgebraicDataTypes {

  class Age(val value: Int)            extends AnyVal  //value class
  final case class Name(value: String) extends AnyVal {
    def greeting: String = s"Hello, $value!"
  }

  final case class Surname(value: String) extends AnyVal
  type SurnameAlias = String

  // Exercise. Rewrite the product type `Person`, so that it uses value classes.
  final case class Person(name: Name, surname: Surname, age: Age)

  // Exercise. Create a smart constructor for `GameLevel` that only permits levels from 1 to 80 (inclusive).
  final case class GameLevel private (value: Int) extends AnyVal
  object GameLevel {
    def create(value: Int): Option[GameLevel] =
      if (value > 80 || value < 1) None
      else Some(GameLevel(value))   //or Option.when(value <= 80 && value <= 1)(GameLevel(value))
  }

  // To disable creating case classes in any other way besides smart constructor, the following pattern
  // can be used. However, it is rather syntax-heavy and cannot be combined with value classes.
  sealed abstract case class Time private (hour: Int, minute: Int)
  object Time {
    def create(hour: Int, minute: Int): Either[String, Time] =
      (hour, minute) match {
        case (hour, _) if hour > 23 || hour < 0 => Left("Invalid hour value")
        case (_, minute) if minute > 59 || minute < 0 => Left("Invalid minute value")
        case _ => Right(new Time(hour, minute) {})
      }
  }

  // Exercise. Implement the smart constructor for `Time` that only permits values from 00:00 to 23:59 and
  // returns "Invalid hour value" or "Invalid minute value" strings in `Left`


  final case class AccountNumber(value: String) extends AnyVal
  final case class CardNumber(value: String)    extends AnyVal
  final case class ValidityDate(month: Int, year: Int)
  sealed trait PaymentMethod
  object PaymentMethod {
    final case class BankAccount(accountNumber: AccountNumber)                      extends PaymentMethod
    final case class CreditCard(cardNumber: CardNumber, validityDate: ValidityDate) extends PaymentMethod
    final case object Cash                                                          extends PaymentMethod
  }

  import PaymentMethod._

  final case class PaymentStatus(value: String) extends AnyVal
  trait BankAccountService {
    def processPayment(amount: BigDecimal, accountNumber: AccountNumber): PaymentStatus
  }
  trait CreditCardService {
    def processPayment(amount: BigDecimal, creditCard: CreditCard): PaymentStatus
  }
  trait CashService {
    def processPayment(amount: BigDecimal): PaymentStatus
  }

  // Exercise. Implement `PaymentService.processPayment` using pattern matching and ADTs.
  class PaymentService(
                        bankAccountService: BankAccountService,
                        creditCardService: CreditCardService,
                        cashService: CashService,
                      ) {
    def processPayment(amount: BigDecimal, method: PaymentMethod): PaymentStatus = {
      method match {
        case BankAccount(accountNumber) => bankAccountService.processPayment(amount, accountNumber)
        case CreditCard(cardNumber, validityDate) => creditCardService.processPayment(amount, CreditCard(cardNumber, validityDate))
        case Cash => cashService.processPayment(amount)
      }
    }
  }
}
