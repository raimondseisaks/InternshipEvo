package reisaks.grokkingFP

case class User(name: String, city: Option[String], favoriteArtists: List[String])

object practisingDataModeling extends App {
  val users = List(
    User("Alice", Some("Melbourne"), List("Bee Gees")),
    User("Bob", Some("Lagos"), List("Bee Gees")),
    User("Eve", Some("Tokyo"), List.empty),
    User("Mallory", None, List("Metallica", "Bee Gees")),
    User("Trent", Some("Buenos Aires"), List("Led Zeppelin"))
  )
  def liveMelbourne(users: List[User]): List[User] = {
    users.filter(w => w.city == Some("Melbourne") || w.city == None)
  }
  def liveLagos(users: List[User]): List[User] = ???
  def likeBeesGees(users: List[User]): List[User] = ???
  def liveCitiesStartsT(users: List[User]): List[User] = ???
  def likeArtistsLonger8(users: List[User]): List[User] = ???
  def likeArtistsStartsM(users: List[User]): List[User] = ???
  println(liveMelbourne(users))
}
