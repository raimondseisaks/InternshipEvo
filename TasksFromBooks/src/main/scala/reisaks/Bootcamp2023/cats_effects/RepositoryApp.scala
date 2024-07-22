package reisaks.Bootcamp2023.cats_effects

import cats.effect.IO

import scala.concurrent.Future
import scala.jdk.CollectionConverters._

import cats.effect.unsafe.implicits.global
import scala.concurrent.ExecutionContext.Implicits.{global => ec}

object RepositoryApp extends App {
  case class User(id: String)

  // TODO: implement in memory dao
  // doesn't have to be thread safe
  class UserDao {
    var users: List[User] = Nil  //Only for learning purposes

    def getAllUsers: Future[List[User]] = Future(users)

    def addUser(user: User): Future[Unit] = Future{users = user :: users}
  }

  def program: Future[Unit] = {
    val users = new UserDao
    for {
      u1 <- users.getAllUsers
      _ = println(u1)
      _ <- users.addUser(User("Vera"))
      _ <- users.addUser(User("Katya"))
      u2 <- users.getAllUsers
      _ = println(u2)
    } yield ()
  }

  program

  // run IO
  // program.unsafeRunSync()

  // wait for future
  Thread.sleep(500)
}
