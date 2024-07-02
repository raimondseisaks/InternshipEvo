import org.scalatest.funsuite.AnyFunSuite

import java.io.File
import scala.io.Source
import scala.sys.process._

class PokerHandEvaluatorTest extends AnyFunSuite {

  test("Check if two files have the same content line by line") {

    val command = Seq("scala", "reisaks.PokerHandEvaluator.Main")
    val inputFile = new File("src/test/scala/PokerHandEvaluatorTest/testSrc/input02.txt")
    Process(command).#<(inputFile).!!

    val file1 = new File("src/test/scala/PokerHandEvaluatorTest/testSrc/output.txt")
    val file2 = new File("src/test/scala/PokerHandEvaluatorTest/testSrc/output02.txt")

    val lines1 = readLinesFromFile(file1)
    val lines2 = readLinesFromFile(file2)

    lines1.zip(lines2).zipWithIndex.foreach { case ((line1, line2), idx) =>
      withClue(s"Comparison failed at line $idx: '$line1' != '$line2'") {
        assert(line1 == line2)
      }
    }

    file1.delete()
  }

  def readLinesFromFile(file: File): List[String] = {
    val source = Source.fromFile(file)
      source.getLines().toList
  }
}



