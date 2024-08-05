package reisaks.PokerHandEvaluator

import reisaks.PokerHandEvaluator.Attributes._
import reisaks.PokerHandEvaluator.CombinationService._
import reisaks.PokerHandEvaluator.Printable._

sealed trait PokerType

case object TexasHoldem extends PokerType
case object OmahaHoldem extends PokerType
case object FiveDrawCard extends PokerType




object pokerTypes {
  def evaluateTexas(board: String, hands: List[String]): Either[GameError, String] = {
    Board.create(board) match {
      case Right(roundBoard) =>
        val roundCards = hands.map(w => Hand.create(w, TexasHoldem))
        if (roundCards.forall(_.isRight)) {
          val bestComb = roundCards.collect {
            case Right(hand) => bestCombination(hand,TexasHoldem, Some(roundBoard))
          }
          val str = makePrintableCombinations(bestComb, "")
          Right(str)
        } else {
          Left(WrongCardHand)
        }
      case Left(error) => Left(error)
    }
  }


  def evaluateOhama(board: String, hands: List[String]): Either[GameError, String] = {
    Board.create(board) match {
      case Right(roundBoard) =>
        val roundCards = hands.map(w => Hand.create(w,OmahaHoldem))
        if (roundCards.forall(_.isRight)) {
          val bestComb = roundCards.collect {
            case Right(hand) => bestCombination(hand, OmahaHoldem, Some(roundBoard))
          }
          val str = makePrintableCombinations(bestComb, "")
          Right(str)
        } else {
          Left(WrongCardHand)
        }
      case Left(error) => Left(error)
    }
  }

  def evaluateFiveCardDraw(hands: List[String]): Either[GameError, String] = {
    val roundCards = hands.map(w => Hand.create(w, FiveDrawCard))
    if (roundCards.forall(_.isRight)) {
      val bestComb = roundCards.collect {
        case Right(hand) => bestCombination(hand, FiveDrawCard)
      }
      val str = makePrintableCombinations(bestComb, "")
      Right(str)
    } else {
      Left(WrongCardHand)
    }
  }
}
