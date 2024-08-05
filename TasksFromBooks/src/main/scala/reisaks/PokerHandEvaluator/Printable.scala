package reisaks.PokerHandEvaluator
import CombinationService._
import combinationSorting._

import scala.annotation.tailrec

object Printable {
  @tailrec
  def makePrintableCombinations(hands: List[Combination], acc: String): String = {
    if (hands.isEmpty) acc
    else {
      val minScore = hands.map(_.combinationScore).min
      val minScoreHands = hands.filter(_.combinationScore == minScore)

      val smallestHand = findWorstCombination(minScoreHands)
      val sortedMinScoreHands = smallestHand.sortBy(_.hand.rawHand)
      val weakestHand = sortedMinScoreHands.head

      val remainingHands = hands.filterNot { hand =>
        hand.sortedRanks == weakestHand.sortedRanks &&
          hand.combinationScore == weakestHand.combinationScore
      }

      val newAcc = sortedMinScoreHands.foldLeft(acc) { (accum, hand) =>
        val separator =
          if (hand == sortedMinScoreHands.last) {
            if (remainingHands.isEmpty) ""
            else " "
          } else {
            "="
          }
        accum + hand.hand.rawHand + separator
      }

      makePrintableCombinations(remainingHands, newAcc)
    }
  }

}
