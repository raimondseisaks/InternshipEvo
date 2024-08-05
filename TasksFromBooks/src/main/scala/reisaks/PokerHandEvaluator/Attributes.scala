package reisaks.PokerHandEvaluator

object Attributes {
  val suits: Map[Char, Suits] = Map('h' -> Heart, 'd' -> Diamond, 'c' -> Club, 's' -> Spade)

  val baseValues: Map[Char, Rank] = Map(
    '2' -> two, '3' -> three, '4' -> four, '5' -> five,
    '6' -> six, '7' -> seven, '8' -> eight, '9' -> nine,
    'T' -> ten, 'J' -> jack, 'Q' -> queen, 'K' -> king, 'A' -> ace
  )

  sealed trait Rank {
    def rank: Int
  }

  final case object two extends Rank { def rank: Int = 2 }
  final case object three extends Rank { def rank: Int = 3 }
  final case object four extends Rank { def rank: Int = 4 }
  final case object five extends Rank { def rank: Int = 5 }
  final case object six extends Rank { def rank: Int = 6 }
  final case object seven extends Rank { def rank: Int = 7 }
  final case object eight extends Rank { def rank: Int = 8 }
  final case object nine extends Rank { def rank: Int = 9 }
  final case object ten extends Rank { def rank: Int = 10 }
  final case object jack extends Rank { def rank: Int = 11 }
  final case object queen extends Rank { def rank: Int = 12 }
  final case object king extends Rank { def rank: Int = 13 }
  final case object ace extends Rank { def rank: Int = 14 }

  sealed trait Suits {
    def suit: Char
  }

  final case object Spade extends Suits { def suit: Char = 's' }
  final case object Heart extends Suits { def suit: Char = 'h' }
  final case object Club extends Suits { def suit: Char = 'c' }
  final case object Diamond extends Suits { def suit: Char = 'd' }

  sealed trait Card {
    def cardRank: Rank
    def cardSuit: Suits
  }

  object Card {
    def create(str: String): Either[GameError, Card] = {
      if (str.length != 2) Left(WrongCard)
      else {
        val rankChar = str.charAt(0)
        val suitChar = str.charAt(1)

        (for {
          rank <- baseValues.get(rankChar).filter(r => r.rank >= 2 && r.rank <= 14)
          suit <- suits.get(suitChar)
        } yield new Card {
          override def cardRank: Rank = rank
          override def cardSuit: Suits = suit
        }).toRight(WrongCard)

      }
    }
  }

 final case class Board(cards: List[Card])
  object Board {
    def create(str: String): Either[GameError, Board] = {
      val boardCards = str.sliding(2, 2).toList
      val cards = boardCards.map(Card.create)
      if (cards.forall(_.isRight) && cards.length == 5) Right(new Board(cards.collect {case Right(value) => value}))
      else Left(WrongCard)
    }
  }

  final case class Hand(cards: List[Card], rawHand: String)
  object Hand {
    def create(str: String, pokerType: PokerType): Either[GameError, Hand] = {

      val handCards = str.sliding(2, 2).toList

      val cards = handCards.map(Card.create)

      val isValid = cards.forall(_.isRight) && {
        val cardCount = cards.size
        pokerType match {
          case TexasHoldem => cardCount == 2
          case OmahaHoldem => cardCount == 4
          case FiveDrawCard => cardCount == 5
        }
      }

      if (isValid) {
        val validCards = cards.collect { case Right(card) => card }
        Right(new Hand(validCards, str))
      } else {
        Left(WrongCard)
      }
    }
  }


  sealed trait GameError {     //General errors
    def message: String
  }
  final case object UnknownGame extends GameError {
    override def message: String = "Unrecognized game type"
  }
  final case object WrongCard extends GameError {
    override def message: String = "Wrong card in board"
  }
  final case object NoCards extends GameError {
    override def message: String = "There is no cards"
  }
  final case object NoCardsHands extends GameError {
    override def message: String = "There is no cards in hands"
  }
  final case object WrongCardHand extends GameError {
    override def message: String = "WrongCard in Hand"
  }

}
