package smartbot

import scala.collection.mutable
import scala.util.Random
import scala.collection.mutable.ListBuffer

object Smartbot {
  def main(args: List[String]) {
    println("Hello, world!")
  }

  class Histogram {
    val words = mutable.Map[String, Int]()
    val randGen = new Random()
    var size = 0

    def addWord(word: String) {
      size = size + 1
      val currentVal = words.getOrElseUpdate(word, 0)
      words += word -> (currentVal + 1)
    }

    // weighted random
    // TODO: halp how do i scala
    def sample: String = {
      val rand = randGen.nextInt(size)
      var count = 0
      val chosenPair = words.find { pair =>
        val word = pair._1
        val score = pair._2
        if (count >= rand) {
          true
        }
        else {
          count += score
          false
        }
      }

      chosenPair.get._1
    }
  }

  class MarkovDict(val depth: Int,
                   val links: mutable.Map[List[String], Histogram],
                   val inits: ListBuffer[List[String]]) {

    def linkFor(pattern: List[String]) : Histogram = {
      links.getOrElseUpdate(pattern, { new Histogram })
    }

    def train(sentence: String) = {
      val tokens = tokenize(sentence)
      tokens.sliding(depth+1).foreach { seq =>
        linkFor(seq.take(depth)).addWord(seq.last)
      }
    }

    def tokenize(str: String): List[String] = {
      str.split(" ") toList
    }
  }

  object MarkovDict {
    def empty(depth: Int) = {
      new MarkovDict(depth, mutable.Map[List[String], Histogram](), ListBuffer())
    }
  }
}
