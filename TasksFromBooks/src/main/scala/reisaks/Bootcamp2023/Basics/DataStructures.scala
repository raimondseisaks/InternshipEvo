package reisaks.Bootcamp2023.Basics

object DataStructures {
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

  val totalVegetableCosts: Int = {
    vegetableAmounts.foldLeft(0)((sum, vegetable) => (vegetablePrices.getOrElse(vegetable._1, 10) * vegetable._2) + sum)
  }

  val totalVegetableWeights: Map[String, Int] = { // implement here
    vegetableWeights.flatMap { case (vegetable, weight) =>
      vegetableAmounts.get(vegetable) match {
        case Some(amount) => Some((vegetable, amount * weight))
        case _ => None
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
}
