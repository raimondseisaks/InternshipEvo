package reisaks.Bootcamp2023.Implicits

object ImplicitParameters {

  object EvolutionUtils0 {

    /** Provides an actionable context for specific wallet.
     *
     * It allows typical CRUD operations on the wallet. Let's ignore the
     * transactional details, i.e. it is fine to call `read` an then `update`
     * the wallet.
     */
    trait WalletContext {
      def create: Unit

      def read: Option[BigDecimal]

      def update(amount: BigDecimal): Unit

      def delete: Unit
    }

    // Exercise 1:
    // - Implement `CreditService` and `DebitService` in terms of operations on `WalletContext`.
    // - Implement `AwardService` in terms of `CreditService` and `DebitService` calls.
    class CreditService {

      /** Gives money to wallet, creates a wallet if does not exist yet */
      def credit(context: WalletContext, amount: BigDecimal): Unit =
        context.read match {
          case None =>
            context.create
            context.update(amount)
          case Some(walAmmount) => context.update(walAmmount + amount)
        }

      class DebitService {

        /** Removes money from wallet */
        def debit(context: WalletContext, amount: BigDecimal): Unit =
          context.read match {
            case Some(walAmmount) =>
              if (walAmmount < amount) println("Not enough money")
              else context.update(walAmmount + amount)
            case None => println("There is not bank record of your card")
          }
      }

      class TransferService(creditService: CreditService, debitService: DebitService) {

        /** Either does credit or debit depending on the amount */
        def transfer(context: WalletContext, amount: BigDecimal): Unit =
          if (amount >= 0) creditService.credit(context, amount)
          else debitService.debit(context, amount)
      }
      trait WalletRepository {
        def getWallet(userId: String): WalletContext
      }
      class WalletController(walletRepository: WalletRepository, transferService: TransferService) {
        def bet(userId: String, amount: BigDecimal): Unit   = {
          val walletContext = walletRepository.getWallet(userId)
          transferService.transfer(walletContext, -amount)
        }
        def award(userId: String, amount: BigDecimal): Unit = {
          val walletContext = walletRepository.getWallet(userId)
          transferService.transfer(walletContext, amount)
        }
      }
    }
  }

  object EvolutionUtils2 {

    // Exercise 2:
    // - Add `implicit` keyword into each `(context: WalletContext)` block,
    //   i.e. make it look like `(implicit context: WalletContext)` block.
    // - Ensure the code compiles before going to a next step.
    // - Remove explicit calls in your `transfer` method by calling
    //   `credit(amount)` and `debit(amount)` without specifying the second
    //   parameter.

    trait WalletContext {
      def create: Unit
      def read: Option[BigDecimal]
      def update(amount: BigDecimal): Unit
      def delete: Unit
    }

    class CreditService {
      def credit(amount: BigDecimal)(implicit context: WalletContext): Unit =
        context.read match {
          case None =>
            context.create
            context.update(amount)
          case Some(walAmmount) => context.update(walAmmount + amount)
        }
    }
    class DebitService {
      def debit(amount: BigDecimal)(implicit context: WalletContext): Unit = context.read match {
        case Some(walAmmount) =>
          if (walAmmount < amount) println("Not enough money")
          else context.update(walAmmount + amount)
        case None => println("There is not bank record of your card")
      }
    }
    class TransferService(creditService: CreditService, debitService: DebitService) {
      def transfer(amount: BigDecimal)(implicit context: WalletContext): Unit =
        if (amount >= 0) creditService.credit(amount)
        else debitService.debit(amount)
    }
    
    trait WalletRepository {
      def getWallet(userId: String): WalletContext
    }
    class WalletController(walletRepository: WalletRepository, transferService: TransferService) {

      def bet(userId: String, amount: BigDecimal): Unit   = {
        implicit val walletContext = walletRepository.getWallet(userId)
        transferService.transfer(amount)
      }

      def award(userId: String, amount: BigDecimal): Unit = {
        implicit val walletContext = walletRepository.getWallet(userId)
        transferService.transfer(amount)
      }
    }
  }
}
