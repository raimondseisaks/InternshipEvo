package reisaks.Bootcamp2023.cats_effects

import reisaks.Bootcamp2023.cats_effects.EffectsHomework1.IO.unit

import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}

/*
 * Homework 1. Provide your own implementation of a subset of `IO` functionality.
 *
 * Provide also tests for this functionality in EffectsHomework1Spec (which you should create).
 *
 * Refer to:
 *  - https://typelevel.org/cats-effect/datatypes/io.html
 *  - https://typelevel.org/cats-effect/api/cats/effect/IO$.html
 *  - https://typelevel.org/cats-effect/api/cats/effect/IO.html
 * about the meaning of each method as needed.
 *
 * There are two main ways how to implement IO:
 * - Executable encoding  - express every constructor and operator for our model in terms of its execution
 * - Declarative encoding - express every constructor and operator for our model as pure data in a recursive
 *                          tree structure
 *
 * While the real Cats Effect IO implementation uses declarative encoding, it will be easier to solve this
 * task using executable encoding, that is:
 *  - Add a `private val run: () => A` parameter to the class `IO` private constructor
 *  - Have most of the methods return a `new IO(...)`
 *
 * Ask questions in the bootcamp chat if stuck on this task.
 */
object EffectsHomework1 {
  final class IO[A] private (private val run: () => A) {
    def map[B](f: A => B): IO[B]                                            = IO(f(run()))
    def flatMap[B](f: A => IO[B]): IO[B]                                    = f(run())
    def *>[B](another: IO[B]): IO[B]                                        = this.flatMap(_ => another)
    def as[B](newValue: => B): IO[B]                                        = this.map(_ => newValue)
    def void: IO[Unit]                                                      = this.map(_ => unit)
    def attempt: IO[Either[Throwable, A]]                                   =
      IO(try Right(run())
      catch { case t: Throwable => Left(t) }
    )
    def option: IO[Option[A]]                                               = IO(
      try Some(run())
      catch { case _: Throwable => None }
    )
    def handleErrorWith[AA >: A](f: Throwable => IO[AA]): IO[AA]            = IO(
      try run()
      catch { case t: Throwable => f(t).run() }
    )
    def redeem[B](recover: Throwable => B, map: A => B): IO[B]              =
      IO(
        try map(run())
        catch { case t: Throwable => recover(t) }
      )
    def redeemWith[B](recover: Throwable => IO[B], bind: A => IO[B]): IO[B] =
      IO(
        try bind(run()).run()
        catch { case t: Throwable => recover(t).run() }
      )
    def unsafeRunSync(): A                                                  = run()
    def unsafeToFuture(): Future[A]                                         = {
      val promise = Promise[A]()
      try {
        promise.success(run())
      } catch {
        case t: Throwable => promise.failure(t)
      }
      promise.future
    }
  }


  object IO {
    def apply[A](body: => A): IO[A]                                   = new IO(() => body)
    def suspend[A](thunk: => IO[A]): IO[A]                            = new IO(thunk.run)
    def delay[A](body: => A): IO[A]                                   = new IO(() => body)
    def pure[A](a: A): IO[A]                                          = IO(a)
    def fromEither[A](e: Either[Throwable, A]): IO[A]                 =
      e match {
        case Right(value) => IO(value)
        case Left(value) => IO.raiseError(value)
      }
    def fromOption[A](option: Option[A])(orElse: => Throwable): IO[A] =
      option match {
        case Some(value) => IO(value)
        case _ => IO.raiseError(orElse)
      }
    def fromTry[A](t: Try[A]): IO[A]                                  =
      t match {
        case Success(value) => IO(value)
        case Failure(exception) => IO.raiseError(exception)
      }
    def none[A]: IO[Option[A]]                                        = IO.pure(None)
    def raiseError[A](e: Throwable): IO[A]                            = IO(throw e)
    def raiseUnless(cond: Boolean)(e: => Throwable): IO[Unit]         = if (!cond) raiseError(e) else IO(unit)
    def raiseWhen(cond: Boolean)(e: => Throwable): IO[Unit]           = if (cond) raiseError(e) else IO(unit)
    def unlessA(cond: Boolean)(action: => IO[Unit]): IO[Unit]         = if (cond) IO(unit) else action
    def whenA(cond: Boolean)(action: => IO[Unit]): IO[Unit]           = if (!cond) IO(unit) else action
    def unit: IO[Unit]                                                = IO.pure(())
  }
}