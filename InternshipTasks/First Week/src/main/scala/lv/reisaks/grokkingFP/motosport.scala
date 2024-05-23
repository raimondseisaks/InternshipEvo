package lv.reisaks.grokkingFP
import java.util.ArrayList

object motosport {
  def totalTime(laptimes : List[Double]) : Double = {
    if (laptimes.isEmpty) 0.0
    else {
      val laptimes2 = laptimes.tail
      if (laptimes2.isEmpty) 0.0
      else laptimes2.sum
    }
  }
  def avgTime(laptimes : List[Double]) : Double = {
    if (laptimes.isEmpty) 0.0
    else {
      val laptimes2 = laptimes.tail
      if (laptimes2.isEmpty) 0.0
      else {
        val sum = laptimes2.sum
        val laps = laptimes2.size
        val num = sum / laps
        val roundedNumber = Math.round(num * 100.0) / 100.0
        roundedNumber
      }
    }
  }
}


object TestProgramm extends App {
  val test1 = List(31.0, 20.9, 21.1, 21.3)
  println("Total : " + motosport.totalTime(test1) + " Avg : " + motosport.avgTime(test1))  // Total : 63.3 Avg : 21.1
  val test2 = List()
  println("Total : " + motosport.totalTime(test2) + " Avg : " + motosport.avgTime(test2))  // Total : 0.0 Avg : 0.0 (Empty list)
  val test3 = List(10.0)
  println("Total : " + motosport.totalTime(test3) + " Avg : " + motosport.avgTime(test3))  // Total : 0.0 Avg : 0.0 (First lap doesn't count)

}