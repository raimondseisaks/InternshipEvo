package reisaks.grokkingFP

object wordScore extends App {
  val input = List("scala", "rust", "ada")
  println(input.sortBy(wordScoreInterface.getLength))
  println(input.sortBy(wordScoreInterface.getCountOfS).reverse)
  println(List(5,1,2,4,3, -2, 0).sortBy(wordScoreInterface.sortIntDescending))
  println(input.sortBy(wordScoreInterface.getCountOfS))
}

object wordScoreInterface {
  def getLength(string: String): Int = {
    string.length
  }
  def getCountOfS(string: String): Int = {
    string.count(c => c == 'A')
  }
  def sortIntDescending(int: Int): Int = -int
}

/**************************************************************/

object rankingWords extends App {
  val words = List("scala", "haskell", "rust", "java", "ada")
  println(rankedWords(w => score(w) + bonus(w) - penalty(w), words))

  def penalty(word: String): Int = {
    if (word.contains("c")) 7 else 0
  }
  def bonus(word: String): Int = if (word.contains("c")) 5 else 0
  def rankedWords(wordScore: String => Int, words: List[String]): List[String] = {
    words.sortBy(wordScore).reverse
  }
  def score(word: String): Int = word.replaceAll("a", "").length
}

/*********************************************************************************/

object practisingMap extends App {
  val input = List("scala", "rust", "ada")
  println(input.map(w => w.length))
  println(input.map(w => w.length - w.replaceAll("s", "").length))
  println(List(5, 1, 2, 4, 0).map(w => -w))
  println(List(5, 1, 2, 4, 0).map(w=> 2*w))
}

/********************************************************************************/

object practisingFilter extends App {
  println(List("scala", "rust", "ada").filter(w => w.length < 5))
  println(List("scala", "rust", "ada").filter(w => w.length - w.replaceAll("s", "").length > 2))
  println(List(5, 1, 2, 4, 0).filter(w => w % 2 == 1))
  println(List(5, 1, 2, 4, 0).filter(w => w > 4))
}

/*******************************************************************************/

object returningFunctions extends App {
  def returnLarger(list: List[Int])(higherThan: Int): List[Int] = {
    list.filter(w => w > higherThan)
  }
  println(returnLarger(List(5, 1, 2, 4, 0))(4))

  def returnDivisible (list: List[Int])(divisible: Int): List[Int] = {
    list.filter(w => w % divisible == 0)
  }
  println(returnDivisible(List(5, 1, 2, 4, 15))(5))

  def largerStringThan (number: Int): String => Boolean = {
   word => word.length < number
  }

  println(List("scala", "ada").filter(largerStringThan(4)))
  println(List("scala", "ada").filter(largerStringThan(7)))

  def returnMoreThanS (list: List[String])(treshold: Int): List[String] = {
    list.filter(w => w.length - w.replaceAll("s", "").length > treshold)
  }

  println(returnMoreThanS(List("rust", "ada"))(2))

}

/*********************************************************************/

object practisingFoldLeft extends App {
  println(List(5, 1, 2, 4, 100).foldLeft(0)((sum, w) => sum + w)) // or println(List(5, 1, 2, 4, 100).sum)
  println(List("scala", "rust", "ada").foldLeft(0)((total, w) => total + w.length))
  println(List("scala", "haskell", "rust", "ada").foldLeft(0)((total, w) => total + w.length - w.replaceAll("s", "").length))
  println(List(5, 1, 2, 4, 15).foldLeft(Int.MinValue)((max, w) => if (w > max) w else max))
}