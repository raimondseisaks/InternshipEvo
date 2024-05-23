package lv.reisaks.grokkingFP

object TipCalculator {
  def getTipPercentage(names: List[String]): Int = {
    if (names.size > 5) 20
    else if (names.size > 0) 10
    else 0
  }
}

object testProgramm extends App {
  val group1 = List("Jackob", "Jane")
  println(TipCalculator.getTipPercentage(group1))    //10
  val group2 = List()
  println(TipCalculator.getTipPercentage(group2))    //0
  val group3 = List("a", "b", "c", "d", "r", "g")
  println(TipCalculator.getTipPercentage(group3))    //20
}