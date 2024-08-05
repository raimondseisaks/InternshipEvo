package reisaks.PokerHandEvaluator
import Attributes._
import scala.io.StdIn
import pokerTypes._

object Main {
  def main(args: Array[String]): Unit = Iterator.continually(Option(StdIn.readLine()))
    .takeWhile(_.nonEmpty)
    .foreach { x => x map Solver.process foreach println }
}

object Solver {
  def process(line: String): String = {
    line.split("\\s+").toList match {

      case "texas-holdem" :: Nil              => NoCards.message

      case "texas-holdem" :: board :: Nil     => NoCardsHands.message

      case "texas-holdem" :: board :: hands   => evaluateTexas(board, hands) match {
        case Left(error) => error.message
        case Right(result) => result
      }

      case "omaha-holdem" :: Nil              => NoCards.message

      case "omaha-holdem" :: board :: Nil     => NoCardsHands.message

      case "omaha-holdem" :: board :: hands   => evaluateOhama(board, hands) match {
        case Left(error)    => error.message
        case Right(result)  => result
      }

      case "five-card-draw" :: Nil            => NoCards.message

      case "five-card-draw" :: hands => evaluateFiveCardDraw(hands) match {
        case Left(error)    => error.message
        case Right(result)  => result
      }

      case _                                  => UnknownGame.message
    }
  }
}
