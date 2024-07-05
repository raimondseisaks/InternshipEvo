package reisaks.Bootcamp2023.Implicits

import scala.language.implicitConversions

//Implicit conversions not a good approach and in most cases not usable
object ImplicitConversions extends App {

  def increment(value: Int): Int = value * 2
  implicit def stringToInt(value: String): Int = Integer.parseInt(value)
  println(increment("7")) // 14

}
