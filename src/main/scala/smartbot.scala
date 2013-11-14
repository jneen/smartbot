package smartbot

import scala.collection.mutable
import scala.util.Random
import scala.collection.mutable.ListBuffer
import scala.annotation.tailrec

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

    def linkFor(pattern: Array[String]) : Histogram = {
      links.getOrElseUpdate(pattern toList, { new Histogram })
    }

    def train(sentence: String) = {
      val tokens = tokenize(sentence)
      tokens.sliding(depth+1).foreach { seq =>
        linkFor(seq.take(depth)).addWord(seq.last)
      }
    }

    def tokenize(str: String): Array[String] = {
      str.split(" ")
    }

    def detokenize(tokens: Array[String]): String = {
      tokens.mkString(" ")
    }

    @tailrec
    private def generateFromTokens(tokens: Array[String]) : String = {
      if (tokens.size > 50) return detokenize(tokens)

      links.get(tokens.takeRight(depth) toList) match {
        case Some(hist) => generateFromTokens(tokens :+ hist.sample)
        case _ => detokenize(tokens)
      }
    }

    def generateSentence(seed: String) : String = {
      generateFromTokens(tokenize(seed))
    }
  }

  object MarkovDict {
    def empty(depth: Int) = {
      new MarkovDict(depth, mutable.Map[List[String], Histogram](), ListBuffer())
    }
  }
}
