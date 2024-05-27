package reisaks.PokerHandEvaluator
import scala.io.StdIn


object Main {
  def main(args: Array[String]): Unit = Iterator.continually(Option(StdIn.readLine()))
    .takeWhile(_.nonEmpty) .foreach { x =>
      x map Solver.process foreach println }
}

object Solver {
    def process(line: String): String = {
      val ErrorPrefix = "Error: "
      line.split("\\s+").toList match {
        case "texas-holdem" :: board :: hands => evaluateTexas(board, hands)
        case "omaha-holdem" :: board :: hands => evaluateOmaha(board, hands)
        case "five-card-draw" :: hands => evaluateFiveCard(hands)
        case x :: _ => ErrorPrefix + "Unrecognized game type"
        case _ => ErrorPrefix + "Invalid input"
      }
    }

  import java.io.{FileWriter, PrintWriter}

  def evaluateTexas(board: String, hands: List[String]): String = {

    val handValuesMap: Map[String, Int] = hands.map { hand =>
      val fullHand = (board + hand).grouped(2).toList
      val combinations = fullHand.combinations(5).toList
      val permutationsOfFive = combinations.flatMap(_.permutations)

      val maxHandValue = permutationsOfFive.map { permutation =>
        val handStr = permutation.mkString("")

        val ranks = handStr.grouped(2).map(_.head).toList

        val suits = handStr.grouped(2).map(_.last).toList

        val handTypeValue = (ranks, suits) match {
          case (r, s) if isStraightFlush(r, s) => 580 + ranksSum(r)
          case (r, _) if isFourOfAKind(r) => 510 + ranksSum(r)
          case (r, _) if isFullHouse(r) => 430 + ranksSum(r)
          case (_, s) if isFlush(s) => 360 + ranksSum(ranks)
          case (r, _) if isStraight(r) => 290 + ranksSum(r)
          case (r, _) if isThreeOfAKind(r) => 220 + ranksSum(r)
          case (r, _) if isTwoPair(r) => 140 + ranksSum(r)
          case (r, _) if isOnePair(r) => 70 + ranksSum(r)
          case _ => 0 + ranksSum(ranks)
        }
        handTypeValue
      }.maxOption.getOrElse(0)

      (hand, maxHandValue)
    }.toMap
    val sortedList = handValuesMap.toList.sortBy(_._2)

    writeIn(sortedList)

    sortedList.sorted.mkString(" ")
  }

  def evaluateOmaha(board: String, hands: List[String]): String =
      //TO DO
      hands.sorted.mkString(" ")

    def evaluateFiveCard(hands: List[String]): String = {
      val handValuesMap: Map[String, Int] = hands.map { hand =>
        val handStr = hand.mkString("")
        val ranks = handStr.grouped(2).map(_.head).toList
        val suits = handStr.grouped(2).map(_.last).toList

          val handTypeValue = (ranks, suits) match {
            case (r, s) if isStraightFlush(r, s) => 580 + ranksSum(r)
            case (r, _) if isFourOfAKind(r) => 510 + ranksSum(r)
            case (r, _) if isFullHouse(r) => 430 + ranksSum(r)
            case (_, s) if isFlush(s) => 360 + ranksSum(ranks)
            case (r, _) if isStraight(r) => 290 + ranksSum(r)
            case (r, _) if isThreeOfAKind(r) => 220 + ranksSum(r)
            case (r, _) if isTwoPair(r) => 140 + ranksSum(r)
            case (r, _) if isOnePair(r) => 70 + ranksSum(r)
            case _ => 0 + ranksSum(ranks)
          }
        (hand, handTypeValue)
      }.toMap
      // Group hands by their values
      val sortedList = handValuesMap.toList.sortBy(_._2)

      writeIn(sortedList)

      sortedList.sortBy(_._2).mkString("")
    }


    def isStraightFlush(ranks: List[Char], suits: List[Char]): Boolean = {
      isFlush(suits) && isStraight(ranks)
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

  def isThreeOfAKind(chars: List[Char]): Boolean = {
    chars.groupBy(identity).values.exists(_.length == 3)
  }


  def isTwoPair(ranks: List[Char]): Boolean = {
      ranks.groupBy(identity).count(_._2.length == 2) == 2
    }

    def isOnePair(ranks: List[Char]): Boolean = {
      ranks.groupBy(identity).count(_._2.length == 2) == 1
    }

  def ranksSum(ranks: List[Char]): Int = {
    val baseValues = Map(
      '2' -> 2, '3' -> 3, '4' -> 4, '5' -> 5, '6' -> 6,
      '7' -> 7, '8' -> 8, '9' -> 9, 'T' -> 10, 'J' -> 11,
      'Q' -> 12, 'K' -> 13, 'A' -> 14
    )

    ranks.map(baseValues).sum
  }
  def writeIn(list: List[(String, Int)]): Unit = {
    val writer = new PrintWriter(new FileWriter("output.txt", true))
    val groupedByValue = list.groupBy(_._2).toList.sortBy(_._1)
    groupedByValue.foreach { case (_, hands) =>
      if (hands.length > 1) {
        val handsStr = hands.sorted.map(_._1).mkString("=")
        writer.print(s"$handsStr ")
      } else {
        writer.print(s"${hands.head._1} ")
      }
    }

    writer.println()
    writer.close()
  }

}





