import scala.io.StdIn.readInt
import scala.io.StdIn.readDouble
import scala.io.StdIn.readLine

case class driving(speed: Double, place: String){
  def isOverSpeeding() : Unit =
    if (speed > 90) println("Sods 50 eiro") else println("Tu esi losis")
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