sealed trait Tree {
  def sum: Int = {
    this match {
      case Node(left, right) => left.sum + right.sum
      case Leaf(elem) => elem
    }
  }

  def double: Tree = {
    this match {
      case Node(left, right) => Node(left.double, right.double)
      case Leaf(elem) => Leaf(elem * 2)
    }
  }
}

case class Node(left: Tree, right: Tree) extends Tree
case class Leaf(elem: Int) extends Tree




