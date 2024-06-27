package reisaks.Bootcamp2023.Functions

object Functions2 {
  //
  var count                           = 0
  def id(): Int                       = {
    val newId = count
    count += 1
    newId
  }
  def idPure(count: Int): (Int, Int) = {
    (count, count+1)
  }

  // Implement `identity` which returns its input unchanged. Do not use scala.Predef.identity
  def identity[A](x: A): A = x

  // Question. What do you expect?

  val f1: PartialFunction[List[String], Boolean] = {
    // head :: tail
    case _ :: _ => true
  }

  val result1: Boolean = f1.isDefinedAt(List("false", "true"))  // 1

  val f2: PartialFunction[List[String], Boolean] = {
    case Nil            => false
    // head :: 2nd :: tail
    case _ :: _ :: tail => f1(tail)
  }

  val result2: Boolean = f2.isDefinedAt(List("false", "true")) // 2

  // Exercise. Implement `andThen` and `compose` which pipes the result of one function to the input of another function
  def compose[A, B, C](f: B => C, g: A => B): A => C = (a: A) => f(g(a))

  def andThen[A, B, C](f: A => B, g: B => C): A => C = (a: A) => g(f(a))

  // --

  // Final task.
  // Case classes are Scala's preferred way to define complex data

  val rawJson: String =
    """
      |{
      |   "username":"John",
      |   "address":{
      |      "country":"UK",
      |      "postalCode":45765
      |   },
      |   "eBooks":[
      |      "Scala",
      |      "Dotty"
      |   ]
      |}
  """.stripMargin

  // Representing JSON in Scala as a sealed family of case classes
  // JSON is a recursive data structure
  sealed trait Json

  case class JObject(value: Map[String, Json]) extends Json

  case class JArray(value: Seq[Json]) extends Json

  case class JString(value: String) extends Json

  case class JNumber(value: BigDecimal) extends Json

  case class JBoolean(value: Boolean) extends Json

  case class JEmpty() extends Json // I added

  // Question. What did I miss?

  // --

  // Task 1. Represent `rawJson` string via defined classes
  val data: Json = JObject(
    Map(
      "username" -> JString("John"),
      "address" -> JObject(
        Map(
          "country" -> JString("UK"),
          "postalCode" -> JNumber(45765)
        )
      ),
      "eBooks" -> JArray(
        Seq(
          JString("Scala"),
          JString("Dotty")
        )
      )
    )
  )

  // Task 2. Implement a function `asString` to print given Json data as a json string

  def asString(data: Json): String = {
    data match {
      case JObject(value) =>
        val fields = value.map { case (key, json) => s""""$key":${asString(json)}""" }
        fields.mkString("{", ",", "}")

      case JArray(value) =>
        val elements = value.map(asString)
        elements.mkString("[", ",", "]")

      case JString(value) =>
        s""""$value""""

      case JNumber(value) =>
        value.toString()

      case JBoolean(value) =>
        value.toString

      case JEmpty() =>
        "Empty"
    }
  }

  // Task 3. Implement a function that validate our data whether it contains JNumber with negative value or not

  def isContainsNegative(data: Json): Boolean = {
    data match {
      case JNumber(num) => num < 0
      case JObject(value) => value.values.exists(isContainsNegative)
      case JArray(value) => value.exists(isContainsNegative)
      case _ => false
    }
  }


  // Task 4. Implement a function that return the nesting level of json objects.
  // Note. top level json has level 1, we can go from top level to bottom only via objects

  def nestingLevel(data: Json): Int = {
    data match {
      case JObject(value) =>
        if (value.isEmpty) 1
        else 1 + value.values.map(nestingLevel).max

      case JArray(value) =>
        if (value.exists(_.isInstanceOf[JObject])) value.map(nestingLevel).max
        else 0

      case _ => 0
    }
  }


  // See FunctionsSpec for expected results

  // If expected type is a PF then a pattern matching block will expended to PF implementation

  val pingPongPFImpl: PartialFunction[String, String] = new PartialFunction[String, String] {
    override def isDefinedAt(x: String): Boolean = x match {
      case "ping" => true
      case _      => false
    }

    override def apply(v: String): String = v match {
      case "ping" => "pong"
    }
  }

  // Example of using partial functions:
  val eithers: Seq[Either[String, Double]] = List("123", "456", "789o")
    .map(x => x.toDoubleOption.toRight(s"Failed to parse $x"))

  val errors: Seq[String] = eithers.collect { case Left(x) =>
    x
  }
}
