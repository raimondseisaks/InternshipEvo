package reisaks.grokkingFP

object TestBooks extends App {
  val friends = List("Alice", "Bob", "Charlie")
  def recommendedBooks(friend: String): List[Book] = { val scala = List(
    Book("FP in Scala", List("Chiusano", "Bjarnason")), Book("Get Programming with Scala", List("Sfregola")))
    val fiction = List(
      Book("Harry Potter", List("Rowling")), Book("The Lord of the Rings", List("Tolkien")))
    if(friend == "Alice") scala
    else if(friend == "Bob") fiction
    else List.empty
  }
  val recomendedAuthors = friends.flatMap(recommendedBooks).flatMap(_.authors)
  println(recomendedAuthors)
}

case class Book(title: String, authors: List[String])

/***********************************************************************/

case class Point(x: Int, y: Int)
case class Point3d(x: Int, y: Int, z: Int)
object forComprehensions extends App {
  val xs = List(1)
  val ys = List(2, -7)
  val zs = List(3, 4)
  println (for {
    x <- xs
    y <- ys
  } yield Point(x, y))

  println (for {
    x <- xs
    y <- ys
    z <- zs
  } yield Point3d(x, y, z))

  println(xs.flatMap(x => ys.flatMap(y => zs.map(z => Point3d(x, y, z)))))
}

/***************************************************************************/

object filteringTechniques extends App {
  val points = List(Point(5, 2), Point(1, 1))
  val riskyRadiuses = List(-10, 0, 2)
  def isInside(point: Point, radius: Int): Boolean = {
    radius * radius >= point.x * point.x + point.y * point.y
  }
  println (for {
    r <- riskyRadiuses.filter( w => w > 0)
    point <- points.filter(p => isInside(p, r))
  } yield s"$point is within a radius of $r")       //filter function

  println (for {
    r <- riskyRadiuses
    point <- points.filter(p => isInside(p, r))
    if (r > 0)
  } yield s"$point is within a radius of $r")      //guard expression

  println (for {
    r <- riskyRadiuses
    validatedRadius <- isRadius(r)
    point <- points.filter(p => isInside(p, r))
  } yield s"$point is within a radius of $validatedRadius")   //with extenal function

  def isRadius(i: Int): List[Int] = {
    if (i > 0) List(i) else List.empty
  }
}

/****************************************************************************/

object practisingForComp extends App {
  println(for {
    x <- List(1, 2, 3)
    y <- Set(1)
  } yield x * y)      // List(1,2,3)

  println(for {
    x <- Set(1, 2, 3)
    y <- List(1)
  } yield x * y)      // Set(1,2,3)

  println(for {
    x <- List(1, 2, 3)
    y <- Set(1)
    z <- Set(0)
  } yield x * y * z)  // List(0,0,0)
}

/*************************************************************************/
case class Event(name: String, start: Int, end: Int)

object parsingWithOption extends App {
  def validateName(name: String): Option[String] = if (name.size > 0) Some(name) else None
  def validateEnd(end: Int): Option[Int] = if (end < 3000) Some(end) else None
  def validateStart(start: Int, end: Int): Option[Int] = if (start <= end) Some(start) else None
  def validateLength(start: Int, end: Int, minLength: Int): Option[Int] = if ((end - start) >= minLength) Some(end-start) else None
  def parseLongEvent(name: String, start: Int, end: Int, minLength: Int): Option[Event] =
    for {
    validName <- validateName(name)
    validEnd <- validateEnd(end)
    validStart <- validateStart(start, end)
    validatedLen <- validateLength(validStart, validEnd, minLength)
  } yield Event(validName, validStart, validEnd)

  println(parseLongEvent("Apollo Program", 1961, 1972, 10))    // Some(Event("Apollo Program", 1961, 1972)))
  println(parseLongEvent("Apollo Program", 1972, 1961, 10))    // None
}

/************************************************************************/
case class TvShow(name: String, start: Int, end: Int)
object errorHandlingStrategies {
  def addOrResign(parsedShows: Option[List[TvShow]], newParsedShow: Option[TvShow]): Option[List[TvShow]] =
    for {
      oldShows <- parsedShows
      newShows <- newParsedShow
    } yield oldShows.appended(newShows)
}

/************************************************************************/