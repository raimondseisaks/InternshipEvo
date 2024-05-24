import java.util.Date
import scala.io.StdIn.readDouble
import scala.io.StdIn.readLine

object Testing
case class driving(speed: Double, place: String){
  def isOverSpeeding() : Unit =
    if (speed > 90 & place == "Pilseta") println("Sods 50 eiro") else println("Nav soda")
}


object Main {
  def main(args: Array[String]): Unit = {
    // Reading a string
    println("Speed: ")
    val speed = readDouble()

    // Reading an integer
    println("Enter a place of driving: ")
    val age = readLine()

    val user = new driving(speed, age)
    user.isOverSpeeding()
    // Reading a double
    //println(s"Your height is $height meters.")
  }
}

sealed trait TrafficLight {
  def next: TrafficLight = {
    this match {
      case Red => Green
      case Green => Yellow
      case Yellow => Red
    }
  }
}
case object Red extends TrafficLight
case object Green extends TrafficLight
case object Yellow extends TrafficLight




