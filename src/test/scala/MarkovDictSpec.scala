package test

import org.specs._
import smartbot._

class MarkovDictSpec extends Specification {

  "Markov dict" should {
    "create an empty one" in {
      val empty = MarkovDict.empty(2)
      empty.depth must be_==(2)
      empty.links.size must be_==(0)
    }

    "train" in {
      val dict = MarkovDict.empty(3)
      dict.train("a sentence with words and stuff")
      dict.links.size must be_==(3)
      dict.links must haveKey("a sentence with")
      dict.links must haveKey("sentence with words")
      dict.links must haveKey("with words and")
      val hist = dict.links.get("sentence with words").get

      hist.size must be_==(1)
      hist.sample must be_==("and")
    }

    "train from log messages" in {
      val testFile = "examples/irc_log.log"
      val dict = MarkovDict.trainFromLog(testFile)
      dict.depth must be_==(3)
      dict.links.size must be_!=(0)
      dict.links must haveKey("oh sorry i")

      dict.generateSentence("oh sorry i") must be_==("oh sorry i use vim now")
      dict.generateSentence("because that's") must be_==("because that's a pretty cool way to say it")
    }

    "generateSentence" in {
      val dict = MarkovDict.empty(3)
      dict.train("a sentence with words")
      dict.generateSentence("a SENTENCE with") must be_==("a SENTENCE with words")
    }

    "randSeed" in {
      val dict = MarkovDict.empty(3)
      dict.train("one two three four")
      val seed = dict.randSeed().toList
      println(seed.toList)
      seed must be_==(List("one", "two", "three"))
      dict.generateSentence() must be_==("one two three four")
    }

    "improveSeed" in {
      val dict = MarkovDict.empty(3)
      dict.train("one two three four")
      dict.generateSentence("one two") must be_==("one two three four")
      dict.generateSentence("one three") must be_==("one two three four")
      dict.generateSentence("three") must be_==("one two three four")
      dict.generateSentence("prefix one two three five banana") must be_==("prefix one two three four")
      dict.generateSentence("one two five banana") must be_==("one two three four")
    }
  }
}
