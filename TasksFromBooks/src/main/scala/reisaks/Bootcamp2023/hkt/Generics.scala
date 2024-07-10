package reisaks.Bootcamp2023.hkt

object Generics extends App {
  def secondElementOfGenericList[A](list: List[A]): Option[A] =
    list match {
      case _ :: x :: _ => Some(x)
      case _ => None
    }

  case class Triple[+A](x: A, y: A, z: A) {
    def zip[B](other: Triple[B]): Triple[(A, B)] =
      Triple((this.x, other.x), (this.y, other.y), (this.z, other.z))

    def set[B >: A](index: Triple.Index, value: B): Triple[B] =
      index match {
        case Triple.First => Triple(value, this.y, this.z)
        case Triple.Second => Triple(this.x, value, this.z)
        case Triple.Third => Triple(this.x, this.y, value)
      }
  }

  object Triple {
    def fromList[A](elements: List[A]): Option[Triple[A]] = // exercise 2 : implement
        elements match {
          case List(x,y,z) => Some(Triple(x, y, z))
          case _ => None
        }

    sealed trait Index

    case object First extends Index

    case object Second extends Index

    case object Third extends Index
  }

  trait Walker[-A, M, +R] { // exercise 4 : fill in correct variance annotations
    def init: M

    def next(element: A, previous: M): M

    def stop(last: M): R

    def contramap[B](f: B => A): Walker[B, M, R] = new Walker[B, M, R] { // exercice 5 implement
      def init: M = Walker.this.init

      def next(element: B, previous: M): M = Walker.this.next(f(element), previous)

      def stop(last: M): R = Walker.this.stop(last)
    }

  }

  trait Collection[+A] {

    def walk[M, R](walker: Walker[A, M, R]): R

    def map[B](f: A => B): Collection[B] = new Collection[B] { // exercise 6 : implement
      def walk[M, R](walker: Walker[B, M, R]): R = {
        Collection.this.walk(walker.contramap(f))
      }
    }

    def flatMap[B](f: A => Collection[B]): Collection[B] = new Collection[B] { // HomeWork 2 : implement
      def walk[M, R](walker: Walker[B, M, R]): R = {
        val intermediateWalker = new Walker[A, M, M] {
          def init: M = walker.init

          def next(element: A, previous: M): M = {
            val collectionB = f(element)
            collectionB.walk(new Walker[B, M, M] {
              def init: M = previous

              def next(e: B, p: M): M = walker.next(e, p)

              def stop(last: M): M = last
            })
          }

          def stop(last: M): M = last
        }
        val finalState = Collection.this.walk(intermediateWalker)
        walker.stop(finalState)
      }
    }
  }

  object Collection {
    def apply[A](seq: A*): Collection[A] = new Collection[A] {
      def walk[M, R](walker: Walker[A, M, R]): R = {
        val collection = seq.foldLeft(walker.init)((state, element) => walker.next(element, state))
        walker.stop(collection)
      }
    }
  }
}

