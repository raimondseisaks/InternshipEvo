package reisaks.Bootcamp2023

import scala.annotation.tailrec

object ControlStructures extends App {
  def applyNTimesForInts(f: Int => Int, n: Int): Int => Int = {
    if (n == 0) {
      x: Int => x
    }
    else x => applyNTimesForInts(f: Int => Int, n-1)(f(x))
  }
  def polymorphicApplyForIntsV1[A](f: A => A, n: Int): A => A = {
    x: A => def loop(m: A, acc: Int): A = {
      acc match {
        case 0 => m
        case _ => loop(f(m), acc-1)
      }
    }
      loop(x, n)
  }

  def polymorphicApplyForIntsV2[A](f: A => A, n: Int): A => A = {
    x:A => List.range(0,n).foldLeft(x)((num, _) => f(num))
  }

  val a = Set(0,1,2)
  val b = Set(true, false)
  val AProductB = for {
    x <- a
    y <- b
  } yield (x, y)

  val AUnionB = a.map(Left(_)) ++ b.map(Right(_))

  println(applyNTimesForInts(_ + 1, 4)(3))  //7
  println(polymorphicApplyForIntsV1((x: Int) => x + 1, 4)(3))  //7
  println(polymorphicApplyForIntsV2((x: Int) => x + 2, 4)(3))  //11
  println(AProductB)
  println(AUnionB)
}
