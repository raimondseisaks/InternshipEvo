package reisaks.Bootcamp2023

import scala.collection.immutable.ListMap

object DataStructures extends App {
  def allEqual[A](list: List[A]): Boolean = {
    list match {
      case Nil => true             // Empty list case
      case head :: Nil => true     // One element handle
      case head :: tail => tail.forall(w => w == head) // or list.forall(_ == head)
    }
  }

  val vegetableAmounts = Map(
    "tomatoes"  -> 17,
    "peppers"   -> 234,
    "olives"    -> 32,
    "cucumbers" -> 323,
  )
  val vegetablePrices = Map(
    "tomatoes" -> 4,
    "peppers"  -> 5,
    "olives"   -> 17,
  )

  val vegetableWeights = Map(
    ("pumpkins", 10),
    ("cucumbers", 20),
    ("olives", 2),
  )

  val totalVegitableCosts: Int = {
    vegetableAmounts.foldLeft(0)((sum, vegitable) => (vegetablePrices.getOrElse(vegitable._1, 10) * vegitable._2) + sum)
  }

  val totalVegetableWeights: Map[String, Int] = { // implement here
    vegetableWeights.flatMap { case (vegetable, weight) =>
      vegetableAmounts.get(vegetable) match {
        case Some(amount) => Some((vegetable, amount * weight))
        case None => None
      }
    }
  }

  def allSubsetsOfSizeN[A](set: Set[A], n: Int): Set[Set[A]] = {
    if (set.isEmpty) Set()
    else {
      val a = set.toList.combinations(n).toSet
      a.map(w => w.toSet)
    }
  }

  // Homework
  val input1 = Map("a" -> 1, "b" -> 2, "c" -> 4, "d" -> 1, "e" -> 0, "f" -> 2, "g" -> 2)
  def sortConsideringEqualValues[T](map: Map[T, Int]): List[(Set[T], Int)] = {
    if (map.isEmpty) {
      List()
    }
    else {
      val a = map.groupBy(_._2)
      val b = a.map(w => (w._2.keySet, w._1)).toList
      b.sortBy(_._2)
    }
  }

  val test1 = List(Set("e") -> 0, Set("a", "d") -> 1, Set("b", "f", "g") -> 2, Set("c") -> 4)

  println(allEqual(List(12,11))) //false
  println(allEqual(List())) // true
  println(allEqual(List(1))) // true

  println(totalVegitableCosts)    //Must be 5012
  println(totalVegetableWeights)  //Map(cucumbers -> 6460, olives -> 64)

  println(allSubsetsOfSizeN(Set(1,2,3,4), 2)) //Set(Set(1, 2), Set(1, 3), Set(2, 3))

  assert(sortConsideringEqualValues(input1) == test1)
  assert(sortConsideringEqualValues(Map()) == List())
}
