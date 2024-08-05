package reisaks.PokerHandEvaluator
import CombinationService._

object combinationSorting {
  def compareCombinations(combination1: Combination, combination2: Combination): Int = {
    combination1.sortedRanks.zip(combination2.sortedRanks).foreach { case (elem1, elem2) =>
      if (elem1.rank > elem2.rank) {
        return 1
      } else if (elem1.rank < elem2.rank) {
        return -1
      }
    }
    0
  }

  def findBestCombination(combinationList: List[Combination]): Combination = {
      val bestHand = combinationList.reduce { (combination1, combination2) =>
        if (combination1.combinationScore > combination2.combinationScore) combination1
        else if (compareCombinations(combination1, combination2) >= 0 && combination1.combinationScore == combination2.combinationScore) {
          combination1
        } else combination2
      }
      bestHand
  }

  def findWorstCombination(handList: List[Combination]): List[Combination] = {
    handList.foldLeft(List.empty[Combination]) { (minHands, hand) =>
      minHands match {
        case List() => List(hand)
        case head :: _ =>
          val comparisonResult = compareCombinations(hand, head)
          if (comparisonResult < 0) {
            List(hand)
          } else if (comparisonResult == 0) {
            hand :: minHands
          } else {
            minHands
          }
      }
    }
  }

}
