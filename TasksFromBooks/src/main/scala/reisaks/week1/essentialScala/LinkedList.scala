trait LinkedList[A] {
  def length: Int = {
    this match {
      case ListEnd() => 0
      case ListNode(_, tail) => tail.length + 1
    }
  }

  def contains(value: A): Boolean = {
    this match {
      case ListNode(head, tail) =>
        if (head == value) true
        else tail.contains(value)
      case ListEnd() => false
    }
  }

  def apply(index: Int): A = {
    this match {
      case ListEnd() => throw new Exception("Index out of bounds")
      case ListNode(head, tail) =>
        if (index == 0) head
        else tail(index - 1)
    }
  }
}

final case class ListEnd[A]() extends LinkedList[A]
final case class ListNode[A](head: A, tail: LinkedList[A]) extends LinkedList[A]

object LinkedListTest extends App {
  val list = ListNode(1, ListNode(2, ListNode(3, ListEnd())))

  println(s"Length: ${list.length}")            // Length: 3
  println(s"Contains 2: ${list.contains(2)}")   // Contains 2: true
  println(s"Contains 4: ${list.contains(4)}")   // Contains 4: false
  println(s"Element at index 1: ${list(1)}")    // Element at index 1: 2
}
