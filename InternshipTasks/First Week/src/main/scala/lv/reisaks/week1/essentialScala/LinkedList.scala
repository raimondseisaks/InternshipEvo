package lv.reisaks.week1.essentialScala

trait LinkedList[A] {
  def length: Int =
    this match {
      case End => 0
      case Pair(head,tail) => tail.length + 1
    }
  def contains(value : A) : Boolean =
    this match {
      case Pair(head, tail) =>
        if (head == value) true
        else tail.contains(value)
      case End => false
    }
  def apply(value: Int) : A =
    this match {
      case End => throw new Exception("Bad things happened")
      case Pair(head, tail) =>
        if (value == 0)
          head
        else
          tail(value - 1)

    }
}

final case class End[A]() extends LinkedList[A]
final case class Pair[A](head: A, tail: LinkedList[A]) extends LinkedList[A]
