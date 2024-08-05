package reisaks.PokerHandEvaluator
import reisaks.PokerHandEvaluator.Attributes._
import combinationSorting._


object CombinationService {
  sealed trait Combination {
    def hand: Hand
    def ranks: List[Rank]
    def combinationScore: Int
    def sortedRanks : List[Rank]      //For printing
  }

  final case class StraightFlush(hand: Hand, ranks: List[Rank], combinationScore: Int) extends Combination {
    override def sortedRanks: List[Rank] = ranks.lastOption.toList
  }

  final case class FourOfAKind(hand: Hand, ranks: List[Rank], combinationScore: Int) extends Combination {
    override def sortedRanks: List[Rank] = {
      val fourOfAKind = ranks.groupBy(identity).collectFirst {
        case (rank, occurrences) if occurrences.size == 4 => rank
      }

      val kicker = ranks.groupBy(identity).collectFirst {
        case (rank, occurrences) if occurrences.size == 1 => rank
      }
      List(fourOfAKind, kicker).flatten
    }
  }

  final case class FullHouse(hand: Hand, ranks: List[Rank], combinationScore: Int) extends Combination {
    override def sortedRanks: List[Rank] = {
      val three = ranks.groupBy(identity).collectFirst {
        case (rank, occurrences) if occurrences.size == 3 => rank
      }

      val two = ranks.groupBy(identity).collectFirst {
        case (rank, occurrences) if occurrences.size == 2 => rank
      }
      List(three, two).flatten
    }
  }

  final case class Flush(hand: Hand, ranks: List[Rank], combinationScore: Int) extends Combination {
    override def sortedRanks: List[Rank] = ranks
  }

  final case class Straight(hand: Hand, ranks: List[Rank], combinationScore: Int) extends Combination {
    override def sortedRanks: List[Rank] = ranks.lastOption.toList
  }
  final case class ThreeOfKind(hand: Hand, ranks: List[Rank], combinationScore: Int) extends Combination {
    override def sortedRanks: List[Rank] = {

      val threePair = ranks.groupBy(identity).collectFirst { case (elem, occurrences) if occurrences.size == 3 => elem }

      val remainingValues = ranks.filterNot(threePair.contains)

      List(threePair ++ remainingValues).flatten
    }
  }
  final case class TwoPair(hand: Hand, ranks: List[Rank], combinationScore: Int) extends Combination {
    override def sortedRanks: List[Rank] = {

      val pairs = ranks.groupBy(identity).collect {
        case (rank, occurrences) if occurrences.size == 2 => rank
      }.toList
      val remainingValues = ranks.filterNot(pairs.contains)
      pairs.sortBy(-_.rank) ++ remainingValues
    }
  }

  final case class OnePair(hand: Hand, ranks: List[Rank], combinationScore: Int) extends Combination {
    override def sortedRanks: List[Rank] = {
      val pair = ranks.groupBy(identity).collectFirst { case (elem, occurrences) if occurrences.size == 2 => elem }
      val remainingValues = ranks.filterNot(pair.contains)
      List(pair ++ remainingValues).flatten
    }
  }
  final case class HighCard(hand: Hand, ranks: List[Rank], combinationScore: Int) extends Combination {
    override def sortedRanks: List[Rank] = ranks
  }


  def isStraightFlush(cards: List[Card]): Boolean = {
    isFlush(cards) && isStraight(cards)
  }

  def isFourOfAKind(ranks: List[Card]): Boolean = {
    ranks.groupBy(_.cardRank).values.exists(_.length == 4)
  }

  def isFullHouse(ranks: List[Card]): Boolean = {
    val distinctRanks = ranks.map(w => w.cardRank).distinct
    distinctRanks.length == 2 &&
      (ranks.count(_.cardRank == distinctRanks.head) == 3 ||
        ranks.count(_.cardRank == distinctRanks.last) == 3)

  }

  def isFlush(cards: List[Card]): Boolean = {
    cards.map(_.cardSuit.suit).distinct.length == 1
  }

  def isStraight(ranks: List[Card]): Boolean = {
    val list = ranks.map(w => w.cardRank.rank)
    list.zipWithIndex.forall { case (num, index) => num == index + list.head } || list == List(14,2,3,4,5)
  }

  def isThreeOfAKind(chars: List[Card]): Boolean = {
    chars.groupBy(_.cardRank).values.exists(_.length == 3)
  }

  def isTwoPair(ranks: List[Card]): Boolean = {
    ranks.groupBy(_.cardRank).count(_._2.length == 2) == 2
  }

  def isOnePair(ranks: List[Card]): Boolean = {
    ranks.groupBy(_.cardRank).count(_._2.length == 2) == 1
  }

  def createCombination(permutation: List[Card], hand: Hand): Combination = {
    if (isStraightFlush(permutation))     StraightFlush(hand, permutation.map(_.cardRank), 1600)
    else if (isFourOfAKind(permutation))  FourOfAKind(hand, permutation.map(_.cardRank), 1400)
    else if (isFullHouse(permutation))    FullHouse(hand, permutation.map(_.cardRank), 1200)
    else if (isFlush(permutation))        Flush(hand, permutation.map(_.cardRank), 1000)
    else if (isStraight(permutation))     Straight(hand, permutation.map(_.cardRank), 800)
    else if (isThreeOfAKind(permutation)) ThreeOfKind(hand, permutation.map(_.cardRank), 600)
    else if (isTwoPair(permutation))      TwoPair(hand, permutation.map(_.cardRank), 400)
    else if (isOnePair(permutation))      OnePair(hand, permutation.map(_.cardRank), 200)
    else                                  HighCard(hand, permutation.map(_.cardRank), 0)
  }

  def bestCombination(hand: Hand, pokerType: PokerType, board: Option[Board] = None): Combination = {
    val comb = pokerType match {
      case TexasHoldem =>
        val allCards = hand.cards ++ board.map(_.cards).getOrElse(List.empty)
        allCards
          .combinations(5)
          .toList
          .flatMap(_.permutations)

      case OmahaHoldem =>
        val boardCards = board.map(_.cards).getOrElse(List.empty)
        (for {
          boardCombination <- boardCards.combinations(3)
          handCombination  <- hand.cards.combinations(2)
        } yield {
          boardCombination ++ handCombination
        }).toList
          .flatMap(_.permutations)

      case FiveDrawCard =>
        hand.cards
          .combinations(5)
          .toList
          .flatMap(_.permutations)
    }

    val allCombs = comb.map { permutation =>
      createCombination(permutation, hand)
    }
    findBestCombination(allCombs)
  }



}
