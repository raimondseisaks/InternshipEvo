package lv.reisaks.grokkingFP
//Works on with empty strings and make abbreviation only for first word


object Abbreviate {
  def abbreviate(name: String) : String = {
    val allNames = name.split(" ")
    allNames match {
      case Array("") => "Empty string!"
      case Array(head) => head
      case Array(head, tail @ _*) => {
        val firstLetter = name.charAt(0)
        val surnames = allNames.tail.mkString(" ")
        firstLetter + "." + " " + surnames
      }
      }
    }
  }


object Test3 extends App {
  val abbr1 = "Alonzo Church"
  println(Abbreviate.abbreviate(abbr1))     //A. Church
  val abbr2 = "Raimmonds"
  println(Abbreviate.abbreviate(abbr2))     //Raimonds
  val abbr3 = ""
  println(Abbreviate.abbreviate(abbr3))     //Empty string
  val abbr4 = "John Jackob Doe"
  println(Abbreviate.abbreviate(abbr4))     //J. Jackob Doe
  val abbr5 = "A. Church"
  println(Abbreviate.abbreviate(abbr5))     //A. Church
  val abbr6 = "A Church"
  println(Abbreviate.abbreviate(abbr6))     //A. Church
}
