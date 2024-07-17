package reisaks.Bootcamp2023.typeclass

import cats.kernel.Monoid

import scala.collection.immutable.HashMap


object TypeClassesExamples extends App {

  // 1. Semigroup
  // 1.1. Implement all parts of the typeclass definition
  trait Semigroup[A] {
    def combine(x: A, y: A): A
  }

  implicit class SemigroupOps[A](value: A) {
    def combine(other: A)(implicit semigroup: Semigroup[A]): A = semigroup.combine(value, other)
  }

  // 1.2. Implement Semigroup for Long, String
  object SemigroupInstances {
    implicit val longSemigroup = new Semigroup[Long] {
      def combine(x: Long, y: Long): Long = x + y
    }

    implicit val stringSemigroup = new Semigroup[String] {
      def combine(x: String, y: String): String = s"$x$y"
    }
  }

  import SemigroupInstances._

  println("sdf".combine("asd"))

  // 1.3. Implement combineAll(list: List[A]) for non-empty lists
  def combineAll[A: Semigroup](list: List[A]): A =
    list.reduceLeft((acc, w) => acc.combine(w))

  println(combineAll(List(1L, 2L, 3L)))

  // 2. Monoid
  // 2.1. Implement Monoid which provides `empty` value (like startingElement in previous example) and extends Semigroup
  trait Monoid[A] extends Semigroup[A] {
    def empty: A
  }

  // 2.2. Implement Monoid for Long, String
  object monoidInstances {
    implicit val longMonoid: Monoid[Long] = new Monoid[Long] {
      def combine(x: Long, y: Long): Long = x + y

      def empty: Long = 0L
    }

    implicit val stringMonoid: Monoid[String] = new Monoid[String] {
      def combine(x: String, y: String): String = x + y

      def empty: String = ""
    }


  }
  // 2.3. Implement combineAll(list: List[A]) for all lists
  import monoidInstances._

  def combineAllL[A: Monoid](list: List[A]): A =
    list.foldLeft(implicitly[Monoid[A]].empty)((acc, w) => acc.combine(w))

  println(combineAllL(List(1L, 2L, 3L)))

  // 2.4. Implement Monoid for Option[A]

  implicit def optionMonoid[A](implicit A: Semigroup[A]): Monoid[Option[A]] = new Monoid[Option[A]] {
    override def empty: Option[A] = None

   override def combine(x: Option[A], y: Option[A]): Option[A] =
      (x, y) match {
        case (Some(x), Some(y)) => Option(A.combine(x, y))
        case (Some(x), _) => Some(x)
        case (_, Some(y)) => Some(y)
        case (_ , _) => None
      }
  }

  // 2.5. Implement Monoid for Function1 (for result of the function)
  implicit def function1Monoid[A, B](implicit B: Monoid[B]): Monoid[A => B] = new Monoid[A => B] {
    override def empty: A => B = _ => B.empty

    override def combine(f: A => B, g: A => B): A => B = a => B.combine(f(a), g(a))
  }

  // 3. Functor
  trait Functor[F[_]] {
    def map[A, B](fa: F[A])(f: A => B): F[B]
  }

  object Functor {
    def apply[F[_] : Functor]: Functor[F] = implicitly[Functor[F]]
  }

  implicit class FunctorOps[F[_] : Functor, A](fa: F[A]) {
    def map[B](f: A => B): F[B] = Functor[F].map(fa)(f)
  }

  implicit val optionFunctor: Functor[Option] = new Functor[Option] {
    def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa.map(f)
  }

  // 3.1. Implement Functor for Map values
  implicit def mapFunctor[K]: Functor[Map[K, *]] = new Functor[Map[K, *]] {
    override def map[A, B](fa: Map[K, A])(f: A => B): Map[K, B] = fa.map {
      case (k, v) => k -> f(v)
    }
  }

  // 4. Semigroupal
  // 4.1. Semigroupal provides `product` method,
  // so in combination with Functor we'll be able to call for example `plus` on two Options (its content)
  trait Semigroupal[F[_]] {
    def product[A, B](fa: F[A], fb: F[B]): F[(A, B)]
  }

  // 4.2. Implement Summoner for Semigroupal
  object Semigroupal {
    def apply[F[_] : Semigroupal]: Semigroupal[F] = implicitly
  }

  // 4.3. Implement Syntax for Semigroupal, so later you'll be able to do:
  // (Option(1) product Option(2)) == Some((1, 2))
  implicit class semiOps[F[_] : Semigroupal, A](fa: F[A]) {
    def product[B](fb: F[B]): F[(A, B)] = Semigroupal[F].product(fa, fb)
  }

  // 4.4. Implement Semigroupal for Option
  implicit val semOpt = new Semigroupal[Option] {
    override def product[A, B](fa: Option[A], fb: Option[B]): Option[(A, B)] = {
      (fa, fb) match {
        case (Some(fa), Some(fb)) => Some(fa, fb)
        case _ => None

      }
    }
  }

  // 4.5. Implement `mapN[R](f: (A, B) => R): F[R]` extension method for Tuple2[F[A], F[B]]
  implicit class TupleOps[F[_] : Semigroupal : Functor, A, B](tuple: (F[A], F[B])) {
    def mapN[R](f: (A, B) => R): F[R] =
      (tuple._1 product tuple._2).map(f.tupled)
  }

 println((Option(1), Option(2)).mapN(_ + _) == Some(3))

  // 4.6. Implement Semigroupal for Map
  implicit def semigroupalMap[K]: Semigroupal[({ type L[A] = Map[K, A] })#L] = new Semigroupal[({ type L[A] = Map[K, A] })#L] {
    override def product[A, B](fa: Map[K, A], fb: Map[K, B]): Map[K, (A, B)] = {
      val onlyKeys = fa.keySet.intersect(fb.keySet)
      onlyKeys.map{ k =>
          k -> (fa(k), fb(k))
        }.toMap
      }
    }


  println((Map(1 -> "a", 2 -> "b"), Map(2 -> "c")).mapN(_ + _) == Map(2 -> "bc"))

  // 5. Applicative
  trait Applicative[F[_]] extends Semigroupal[F] with Functor[F] {
    def pure[A](x: A): F[A]
  }

  object Applicative {
    def apply[F[_] : Applicative]: Applicative[F] = implicitly
  }

  implicit class ApplicativeValueOps[F[_] : Applicative, A](a: A) {
    def pure: F[A] = Applicative[F].pure(a)
  }

  // 5.1. Implement Applicative for Option, Either
  implicit val optAppl = new Applicative[Option] {
    override def map[A, B](fa: Option[A])(f: A => B): Option[B] = Functor[Option].map(fa)(f)

    override def pure[A](x: A): Option[A] = Some(x)

    override def product[A, B](fa: Option[A], fb: Option[B]): Option[(A, B)] = Semigroupal[Option].product(fa, fb)
  }

  // 5.2. Implement `traverse` function
  def traverse[A, B](as: List[A])(f: A => Option[B]): Option[List[B]] = as.foldRight(Option(List.empty[B])) { (el, acc) =>
    (acc, f(el)) match {
      case  (Some(acc), Some(el)) => Some (el :: acc)
      case _ => None
    }
  }

  traverse(List(1, 2, 3)) { i =>
    Option.when(i % 2 == 1)(i)
  }.isEmpty

  traverse(List(1, 2, 3)) { i =>
    Some(i + 1)
  }.contains(List(2, 3, 4))


  // 5.3. Implement `traverseA` for all Applicatives instead of Option

  def traverseA[A, B, C](as: List[A])(f: A => Either[B, C]): Either[B, List[C]] = {
    as.foldRight(Right(List.empty[C]): Either[B, List[C]]) { (a, acc) =>
      acc.flatMap { list =>
        f(a) match {
          case Left(err)  => Left(err)
          case Right(value) => Right(value :: list)
        }
      }
    }
  }


  println(traverseA(List(1, 2, 3)) { i =>
     Either.cond(i % 2 == 1, i, "Error")
  } == Left("Error"))

  println(traverseA(List(1, 2, 3)) { i =>
     Right(i + 1): Either[Int, Any]
  } == Right(List(2, 3, 4)))

  // Scala Typeclassopedia: https://github.com/lemastero/scala_typeclassopedia
}
