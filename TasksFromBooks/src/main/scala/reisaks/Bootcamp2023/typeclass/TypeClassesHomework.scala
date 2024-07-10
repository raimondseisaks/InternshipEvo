package reisaks.Bootcamp2023.typeclass

object TypeClassesHomework extends App {

  object OrderingTask {

    final case class Money(amount: BigDecimal)

    implicit val moneyOrdering: Ordering[Money] = new Ordering[Money] {
      def compare(money1: Money, money2: Money): Int =
        money1.amount.compare(money2.amount)
    } // TODO Implement Ordering instance for Money
  }

  object ShowTask {

    trait Show[T] { // Fancy toString
      def show(entity: T): String
    }

    final case class User(id: String, name: String)

    // TODO Implement Show instance for User
    implicit val showForUser: Show[User] = new Show[User] {
      override def show(entity: User): String = s"Show user : ${entity.id} ${entity.name}"
    }

    implicit class ShowOps[A](entity: A) {
      def show(implicit show: Show[A]): String = show.show(entity)
    }

    User("1", "John").show
    // TODO Implement syntax for Show so I can do User("1", "John").show
  }

  object ParseTask {

    type Error = String

    trait Parse[T] { // Feel free to use any format. It could be CSV or anything else.
      def parse(entity: String): Either[Error, T]
    }

    final case class User(id: String, name: String)

    // TODO Implement Parse instance for User
    implicit val parseUser: Parse[User] = new Parse[User] {
      override def parse(entity: String): Either[Error, User] = {
        val data = entity.split(" ").toList
        data match {
          case x :: y :: Nil => Right(User(x, y)) //very simple data interpretation
          case _ => Left("This is obviously not a User")
        }
      }
    }

    implicit class parseOps[A](entity: String) {
      def parseUser(implicit parse: Parse[User]): Either[Error, User] = parse.parse(entity)
    }

    println("lalala".parseUser)
    // TODO Implement syntax for Parse so I can do "lalala".parse[User] (and get an error because it is obviously not a User)
  }

  object EqualsTask {
    // TODO Design a typesafe equals so I can do a === b, but it won't compile if a and b are of different types
    // Define the typeclass (think of a method signature)
    // Keep in mind that `a method b` is `a.method(b)`

    trait typesafeEquals {
      def eq[A](val1: A, val2: A): Boolean
    }


    implicit val typesafeEq: typesafeEquals = new typesafeEquals {
      override def eq[A](val1: A, val2: A): Boolean =
        val1 == val2
    }

    implicit class typesafeEqOps[A](val1: A) {
      def ===(val2: A)(implicit equal: typesafeEquals): Boolean = equal.eq(val1, val2)
    }

    12 === 12
    12 === 13
    //12 === "a"

  }

  println(EqualsTask)

  object Foldable {

    trait Semigroup[A] {
      def combine(x: A, y: A): A
    }

    trait Monoid[A] extends Semigroup[A] {
      def empty: A
    }

    trait Foldable[F[_]] {
      def foldLeft[A, B](as: F[A])(z: B)(f: (B, A) => B): B

      def foldRight[A, B](as: F[A])(z: B)(f: (A, B) => B): B

      def foldMap[A, B](as: F[A])(f: A => B)(implicit monoid: Monoid[B]): B
    }

    implicit val optionFoldable: Foldable[Option] = new Foldable[Option] { // TODO Implement Foldable instance for Option
      override def foldLeft[A, B](as: Option[A])(z: B)(f: (B, A) => B): B =
        as match {
          case Some(value) => f(z, value)
          case None => z
        }

      override def foldRight[A, B](as: Option[A])(z: B)(f: (A, B) => B): B =
        as match {
          case Some(value) => f(value, z)
          case None => z
        }

      override def foldMap[A, B](as: Option[A])(f: A => B)(implicit monoid: Monoid[B]): B =
        as match {
          case Some(value) => f(value)
          case None => monoid.empty
        }
    }

    implicit val listFoldable: Foldable[List] = new Foldable[List] { // TODO Implement Foldable instance for List
      override def foldMap[A, B](as: List[A])(f: A => B)(implicit monoid: Monoid[B]): B =
        as.foldLeft(monoid.empty)((acc, w) => monoid.combine(acc, f(w)))

      override def foldLeft[A, B](as: List[A])(z: B)(f: (B, A) => B): B =
        as match {
          case Nil => z
          case _ => as.foldLeft(z)((acc, w) => f(acc, w))
        }

      override def foldRight[A, B](as: List[A])(z: B)(f: (A, B) => B): B =
        as match {
          case Nil => z
          case _ => as.foldLeft(z)((acc, w) => f(w, acc))
        }

    }

    sealed trait Tree[A]

    object Tree {
      final case class Leaf[A](value: A) extends Tree[A]

      final case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]
    }

    implicit val treeFoldable: Foldable[Tree] = new Foldable[Tree] { // TODO Implement Foldable instance for Tree
      override def foldMap[A, B](as: Tree[A])(f: A => B)(implicit monoid: Monoid[B]): B =
        as match {
          case Tree.Leaf(value) => f(value)
          case Tree.Branch(left, right) => {
            monoid.combine(foldMap(left)(f), foldMap(right)(f))
          }
        }

      override def foldLeft[A, B](as: Tree[A])(z: B)(f: (B, A) => B): B =
        as match {
          case Tree.Branch(left, right) => {
            val leftSide = foldLeft(left)(z)(f)
            foldLeft(right)(leftSide)(f)
          }
          case Tree.Leaf(value) => f(z, value)
        }

      override def foldRight[A, B](as: Tree[A])(z: B)(f: (A, B) => B): B =
        as match {
          case Tree.Branch(left, right) => {
            val leftSide = foldRight(left)(z)(f)
            foldRight(right)(leftSide)(f)
          }
          case Tree.Leaf(value) => f(value, z)
        }
    }
  }

  object ApplicativeTask {

    trait Semigroupal[F[_]] {
      def product[A, B](fa: F[A], fb: F[B]): F[(A, B)]
    }

    trait Functor[F[_]] {
      def map[A, B](fa: F[A])(f: A => B): F[B]
    }

    trait Apply[F[_]] extends Functor[F] with Semigroupal[F] {

      def ap[A, B](fab: F[A => B])(fa: F[A]): F[B] // "ap" here stands for "apply" but it's better to avoid using it

      override def product[A, B](fa: F[A], fb: F[B]): F[(A, B)] = // TODO Implement using `ap` and `map`
        ap(map(fa)(a => (b: B) => (a, b)))(fb)

      def map2[A, B, Z](fa: F[A], fb: F[B])(f: (A, B) => Z): F[Z] = // TODO Implement using `map` and `product`
        map(product(fa, fb)) { case (w1, w2) => f(w1, w2) }
    }

    trait Applicative[F[_]] extends Apply[F] {
      def pure[A](a: A): F[A]
    }

    // TODO Implement Applicative instantce for Option
    implicit val optionApplicative: Applicative[Option] = new Applicative[Option] { // Keep in mind that Option has flatMap
      override def ap[A, B](fab: Option[A => B])(fa: Option[A]): Option[B] =
        fab match {
          case Some(value) => fa.map(value)
          case _ => None
        }

      override def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa.map(f)

      override def map2[A, B, Z](fa: Option[A], fb: Option[B])(f: (A, B) => Z): Option[Z] = fa.flatMap(a => fb.map(b => f(a, b)))

      override def pure[A](a: A): Option[A] = Option(a)
    }


    // TODO Implement traverse using `map2`
    def traverse[F[_] : Applicative, A, B](as: List[A])(f: A => F[B]): F[List[B]] = {
      val instance = implicitly[Applicative[F]]
      as.foldLeft(instance.pure(List.empty[B])) { (acc, a) =>
        instance.map2(acc, f(a)) { (listB, b) =>
          b :: listB
        }
      }
    }


    // TODO Implement sequence (ideally using already defined things)
    def sequence[F[_] : Applicative, A](fas: List[F[A]]): F[List[A]] =
      traverse(fas)(fa => fa)
  }
}
