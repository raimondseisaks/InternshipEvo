package lv.reisaks.grokkingFP

object ListOperations {
  def firstTwo(list : List[String]): List[String] = {
    list.slice(0, 2)
  }
  def lastTwo(list: List[String]): List[String] = {
    list.slice(list.size-2, list.size)
  }
  def movedFirstTwoTheEnd(list: List[String]): List[String] = {
    val firstTwo = list.slice(0, 2)
    val tailPart = list. slice(2, list.size)
    tailPart.appendedAll(firstTwo)
  }
  def insertBeforeLast(list: List[String], elem: String): List[String] = {
    val lastElem = list.last
    val tailPart = list.slice(0, list.size-1)
    tailPart.appended(elem).appended(lastElem)
  }
}

object Test4 extends App{
  val list1 = List("a", "b", "c", "d", "e")
  println(ListOperations.firstTwo(list1)) // List("a", "b")
  println(ListOperations.lastTwo(list1)) // List("b", "c")
  println(ListOperations.movedFirstTwoTheEnd(list1)) // List("c", "d", "e", "a", "b")
  println(ListOperations.insertBeforeLast(list1, "before")) //List("a", "b", "c", "d" "before", "e")
}
