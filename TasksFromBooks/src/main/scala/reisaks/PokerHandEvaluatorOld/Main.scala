package reisaks.PokerHandEvaluatorOld
import scala.io.StdIn
import java.io.{FileWriter, PrintWriter}

object Main {
  def main(args: Array[String]): Unit = Iterator.continually(Option(StdIn.readLine()))
    .takeWhile(_.nonEmpty)
    .foreach { x => x map Solver.process foreach println }
}

object Solver {
  val baseValues: Map[Char, Int] = Map(
    '2' -> 2, '3' -> 3, '4' -> 4, '5' -> 5,
    '6' -> 6, '7' -> 7, '8' -> 8, '9' -> 9,
    'T' -> 10, 'J' -> 11, 'Q' -> 12, 'K' -> 13, 'A' -> 14
  )

  sealed trait Hand {
    def myHand: String

    def ranks: List[Char]

    def combination: Int

    def rankValues: List[Int] = ranks.map(baseValues).sorted.reverse

    def compareList: List[Int]
  }

  case class StraightFlush(ranks: List[Char], myHand: String) extends Hand {
    override val rankValues = if (ranks.head != 'A') ranks.map(baseValues).sorted.reverse else ranks.map(baseValues.updated('A', 1)).sorted.reverse
    val combination = 1600
    val compareList = List(rankValues.head)
  }

  case class FourOfAKind(ranks: List[Char], myHand: String) extends Hand {
    val combination = 1400
    val compareList = List(rankValues.groupBy(identity).collectFirst { case (elem, occurrences) if occurrences.size == 4 => elem }, rankValues.groupBy(identity).collectFirst { case (elem, occurrences) if occurrences.size == 1 => elem }).flatten
  }

  case class FullHouse(ranks: List[Char], myHand: String) extends Hand {
    val combination = 1200
    val compareList = List(rankValues.groupBy(identity).collectFirst { case (elem, occurrences) if occurrences.size == 3 => elem }, rankValues.groupBy(identity).collectFirst { case (elem, occurrences) if occurrences.size == 2 => elem }).flatten
  }

  case class Flush(ranks: List[Char], myHand: String) extends Hand {
    val combination = 1000
    val compareList = rankValues
  }

  case class Straight(ranks: List[Char], myHand: String) extends Hand {
    override val rankValues = if (ranks.head != 'A' && ranks.contains('K')) ranks.map(baseValues).sorted.reverse else ranks.map(baseValues.updated('A', 1)).sorted.reverse
    val combination = 800
    val compareList = List(rankValues.head)
  }

  case class ThreeOfKind(ranks: List[Char], myHand: String) extends Hand {
    val combination = 600
    val threePair = rankValues.groupBy(identity).collectFirst { case (elem, occurrences) if occurrences.size == 3 => elem }
    val remainingValues = rankValues.filterNot(threePair.contains)
    val compareList = List(threePair ++ remainingValues).flatten
  }

  case class TwoPair(ranks: List[Char], myHand: String) extends Hand {
    val combination = 400
    val pairs = rankValues.groupBy(identity).collect { case (elem, occurrences) if occurrences.size == 2 => elem }.toList.sorted(Ordering[Int].reverse).take(2)
    val remainingValues = rankValues.filterNot(pairs.contains)
    val compareList = List(pairs ++ remainingValues).flatten
  }

  case class OnePair(ranks: List[Char], myHand: String) extends Hand {
    val combination = 200
    val pair = rankValues.groupBy(identity).collectFirst { case (elem, occurrences) if occurrences.size == 2 => elem }
    val remainingValues = rankValues.filterNot(pair.contains)
    val compareList = List(pair ++ remainingValues).flatten
  }

  case class HighCard(ranks: List[Char], myHand: String) extends Hand {
    val combination: Int = 0
    val compareList = rankValues
  }

  def process(line: String): String = {
    // Regex for validation
    val cardPattern = "^[2-9TJQKA][hdcs]$".r

    def isValidCard(card: String): Boolean = cardPattern.matches(card)

    //Basic validation for demonstration purposes (Do not cover all possible errors!!!)
    def validateTexasBoardAndHands(board: String, hands: List[String]): Boolean = {
      val validBoard = board.grouped(2).forall(isValidCard) && board.length == 10
      val validHands = hands.forall(hand => hand.grouped(2).forall(isValidCard)) && hands.forall(hand => hand.length == 4)
      val isUnique = (board.grouped(2).toList ++ hands.mkString("").grouped(2)).distinct.size == (board.grouped(2).toList ++ hands.mkString("").grouped(2)).size
      validBoard && validHands && isUnique
    }

    def validateOmahaBoardAndHands(board: String, hands: List[String]): Boolean = {
      val validBoard = board.grouped(2).forall(isValidCard) && board.length == 10
      val validHands = hands.forall(hand => hand.grouped(2).forall(isValidCard)) && hands.forall(hand => hand.length == 8)
      val isUnique = (board.grouped(2).toList ++ hands.mkString("").grouped(2)).distinct.size == (board.grouped(2).toList ++ hands.mkString("").grouped(2)).size
      validBoard && validHands && isUnique
    }

    def validateFiveCardHands(hands: List[String]): Boolean = {
      val isUnique = (hands.mkString("").grouped(2)).distinct.size == (hands.mkString("").grouped(2)).size
      hands.forall(hand => hand.grouped(2).forall(isValidCard)) && hands.forall(hand => hand.length == 10) && isUnique
    }

    val ErrorPrefix = "Error: "
    line.split("\\s+").toList match {
      case "texas-holdem" :: board :: hands if (validateTexasBoardAndHands(board, hands)) => evaluateTexas(board, hands)
      case "omaha-holdem" :: board :: hands if (validateOmahaBoardAndHands(board, hands)) => evaluateOmaha(board, hands)
      case "five-card-draw" :: hands if (validateFiveCardHands(hands)) => evaluateFiveCard(hands)
      case x :: _ => ErrorPrefix + "Invalid Input"
      case _ => "Empty Input"
    }
  }

  def createHandObject(permutation: List[String], hand: String): Hand = { //Create combination object
    val handStr = permutation.mkString("")
    val ranks = handStr.grouped(2).map(_.head).toList
    val suits = handStr.grouped(2).map(_.last).toList
    val handTypeValue = (ranks, suits) match {
      case (r, s) if hand.length == 10 =>
        if (isStraightFlushV2(r, s)) StraightFlush(r, hand)
        else if (isFourOfAKind(r)) FourOfAKind(r, hand)
        else if (isFullHouse(r)) FullHouse(r, hand)
        else if (isFlush(s)) Flush(r, hand)
        else if (isStraightV2(r)) Straight(r, hand)
        else if (isThreeOfAKind(r)) ThreeOfKind(r, hand)
        else if (isTwoPair(r)) TwoPair(r, hand)
        else if (isOnePair(r)) OnePair(r, hand)
        else HighCard(r, hand)

      case (r, s) =>
        if (isStraightFlush(r, s)) StraightFlush(r, hand)
        else if (isFourOfAKind(r)) FourOfAKind(r, hand)
        else if (isFullHouse(r)) FullHouse(r, hand)
        else if (isFlush(s)) Flush(r, hand)
        else if (isStraight(r)) Straight(r, hand)
        else if (isThreeOfAKind(r)) ThreeOfKind(r, hand)
        else if (isTwoPair(r)) TwoPair(r, hand)
        else if (isOnePair(r)) OnePair(r, hand)
        else HighCard(r, hand)
    }
    handTypeValue
  }

  def getBestHand(maxHandValue: List[Hand]): Hand = {
    val maxCombinationValue = maxHandValue.map(_.combination).max
    val handsMax = maxHandValue.filter(_.combination == maxCombinationValue)
    val bestHand1 = findMaxHand(handsMax)
    val bestHand2 = bestHand1.head
    bestHand2
  }

  def evaluateTexas(board: String, hands: List[String]): String = {
    val handValuesMap: List[Hand] = hands.map { hand =>
      val fullHand = (board + hand).grouped(2).toList
      val combinations = fullHand.combinations(5).toList
      val permutationsOfFive = combinations.flatMap(_.permutations)
      val maxHandValue = permutationsOfFive.map { permutation =>
        createHandObject(permutation, hand)
      }
      getBestHand(maxHandValue)
    }
    writeIn(handValuesMap)
  }

  def evaluateOmaha(board: String, hands: List[String]): String = {
    val handValuesMap: List[Hand] = hands.map { hand =>
      val boardCards = board.grouped(2).toList
      val handsCards = hand.grouped(2).toList
      val combinations = (for {
        boardCombination <- boardCards.combinations(3)
        handCombination <- handsCards.combinations(2)
      } yield {
        boardCombination ++ handCombination
      }).toList

      val permutationsOfFive = combinations.flatMap(_.permutations)
      val maxHandValue = permutationsOfFive.map { permutation =>
        createHandObject(permutation, hand)
      }.distinct

      getBestHand(maxHandValue)
    }
    writeIn(handValuesMap)
  }

  def evaluateFiveCard(hands: List[String]): String = {
    val handValuesMap: List[Hand] = hands.map { hand =>
      createHandObject(hand.grouped(2).toList, hand)
    }
    writeIn(handValuesMap)
  }

  def isStraightFlush(ranks: List[Char], suits: List[Char]): Boolean = {
    isFlush(suits) && isStraight(ranks)
  }
  def isStraightFlushV2(ranks: List[Char], suits: List[Char]): Boolean = {
    isFlush(suits) && isStraightV2(ranks)
  }

  def isFourOfAKind(ranks: List[Char]): Boolean = {
    ranks.groupBy(identity).values.exists(_.length == 4)
  }

  def isFullHouse(ranks: List[Char]): Boolean = {
    val distinctRanks = ranks.distinct
    distinctRanks.length == 2 && (ranks.count(_ == distinctRanks.head) == 3 || ranks.count(_ == distinctRanks.last) == 3)
  }

  def isFlush(suits: List[Char]): Boolean = {
    suits.distinct.length == 1
  }

  def isStraight(ranks: List[Char]): Boolean = {
    val str = ranks.mkString("")
    val straightRanks = List("A2345", "23456", "34567", "45678", "56789", "6789T", "789TJ", "89TJQ", "9TJQK", "TJQKA")
    straightRanks.exists(str.contains)
  }

  def isStraightV2(ranks: List[Char]): Boolean = {
    val str = ranks.mkString("")
    val straightRanks = List("A2345", "23456", "34567", "45678", "56789", "6789T", "789TJ", "89TJQ", "9TJQK", "TJQKA")
    val allPermutations = straightRanks.flatMap(w => w.permutations.toList)
    allPermutations.contains(str)
  }

  def isThreeOfAKind(chars: List[Char]): Boolean = {
    chars.groupBy(identity).values.exists(_.length == 3)
  }

  def isTwoPair(ranks: List[Char]): Boolean = {
    ranks.groupBy(identity).count(_._2.length == 2) == 2
  }

  def isOnePair(ranks: List[Char]): Boolean = {
    ranks.groupBy(identity).count(_._2.length == 2) == 1
  }

  def findMaxHand(handList: List[Hand]): List[Hand] = {
    var maxHand = handList.head

    handList.tail.foreach { hand =>
      if (compareHands(hand, maxHand) > 0) {
        maxHand = hand
      }
    }

    List(maxHand)
  }

  def compareHands(hand1: Hand, hand2: Hand): Int = {
    compareLists(hand1.compareList, hand2.compareList)
  }

  def compareLists(list1: List[Int], list2: List[Int]): Int = {
    list1.zip(list2).foreach { case (elem1, elem2) =>
      if (elem1 > elem2) {
        return 1
      } else if (elem1 < elem2) {
        return -1
      }
    }
    0
  }

  def findMinHand(handList: List[Hand]): List[Hand] = {
    var minHands = List(handList.head)
    handList.tail.foreach { hand =>
      val comparisonResult = compareHands(hand, minHands.head)
      if (comparisonResult < 0) {
        minHands = List(hand)
      } else if (comparisonResult == 0) {
        minHands = hand :: minHands
      }
    }
    minHands
  }

  def writeIn(hands: List[Hand]): String = {
    val writer = new PrintWriter(new FileWriter("src/test/scala/PokerHandEvaluatorTest/testSrc/output.txt", true))
    val out = printSmallestHands(hands, "")
    writer.println()
    writer.close()
    out
  }

  def printSmallestHands(hands: List[Hand], acc: String): String = {
    if (hands.nonEmpty) {
      val minCombinationValue = hands.map(_.combination).min
      val handsMix = hands.filter(_.combination == minCombinationValue)
      val smallestHand = findMinHand(handsMix)
      val withoutWeak1 = hands.filterNot(_.combination == smallestHand.head.combination)
      val withoutWeak2 = hands.filter(_.combination == smallestHand.head.combination)
      val withoutWeak3 = withoutWeak2.filterNot(_.compareList == smallestHand.head.compareList)
      val withoutWeak4 = withoutWeak1 ++ withoutWeak3


      val newAcc = if (smallestHand.length > 1) {
        val smallestHandSorted = smallestHand.sortBy(_.myHand)
        smallestHandSorted.foldLeft(acc) { (accum, hand) =>
          if (hand == smallestHandSorted.last) {
            if (withoutWeak4.isEmpty) accum + s"${hand.myHand}"
            else accum + s"${hand.myHand} "
          } else {
            accum + s"${hand.myHand}="
          }
        }
      } else {
        if (withoutWeak4.isEmpty) acc + s"${smallestHand.head.myHand}"
        else acc + s"${smallestHand.head.myHand} "
      }

      printSmallestHands(withoutWeak4, newAcc)
    } else {
      acc
    }
  }
}