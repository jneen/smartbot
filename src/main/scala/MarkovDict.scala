package smartbot

import scala.collection.mutable
import scala.util.Random
import scala.collection.mutable.ListBuffer
import smartbot.LogParser._
import scala.annotation.tailrec

object MarkovDict {

  def empty(depth: Int) = {
    new MarkovDict(depth, mutable.HashMap[List[String], Histogram](), ListBuffer())
  }

  def trainFromLog(file: String): MarkovDict = {
    val (depth, messages) = getMessages(file)
    val dict = empty(depth.getOrElse(3))

    var processed = 0
    messages.foreach { line =>
      processed += 1
      if (processed % 5000 == 0) println(processed)

      dict.train(line)
    }

    dict
  }

}

class MarkovDict(val depth: Int,
                 val links: mutable.HashMap[List[String], Histogram],
                 val inits: ListBuffer[Array[String]]) {

  val randGen = new Random()

  def linkFor(pattern: Array[String]) : Histogram = {
    links.getOrElseUpdate(pattern toList, { new Histogram })
  }

  def train(sentence: String) = {
    val tokens = tokenize(sentence)
    val len = tokens.length

    if (len >= depth + 1) {
      inits.append(tokens.take(depth))

      val patterns = tokens.sliding(depth)

      for ((pat, i) <- patterns.zipWithIndex) {
        // exclude the last n-gram, which has nothing to map to
        if (depth + i < len) {
          val targetWord = tokens(depth+i)
          linkFor(pat).addWord(targetWord)
        }
      }
    }
  }

  def tokenize(str: String): Array[String] = {
    str.split(" ")
  }

  def detokenize(tokens: Array[String]): String = {
    tokens.mkString(" ")
  }

  def randSeed() : Array[String] = {
    inits(randGen.nextInt(inits.size))
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

  def generateSentence() : String = {
    generateFromTokens(randSeed())
  }
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
    require(size > 0)
    val rand = randGen.nextInt(size)
    var count = 0

    words.foreach { pair =>
      val word = pair._1
      val score = pair._2
      count += score
      if (count > rand) return word
    }

    throw new RuntimeException("sample failed to return a value")
  }
}
