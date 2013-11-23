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
      dict.links must haveKey(List("a", "sentence", "with"))
      dict.links must haveKey(List("sentence", "with", "words"))
      dict.links must haveKey(List("with", "words", "and"))
      val hist = dict.links.get(List("sentence", "with", "words")).get

      hist.size must be_==(1)
      hist.sample must be_==("and")
    }

    "train from log messages" in {
      val testFile = "examples/irc_log.log"
      val dict = MarkovDict.trainFromLog(testFile)
      dict.links.size must be_!=(0)
      dict.links must haveKey(List("what", "up", "bitches"))
    }

    "generateSentence" in {
      val dict = MarkovDict.empty(3)
      dict.train("a sentence with words")
      dict.generateSentence("a sentence with") must be_==("a sentence with words")
    }
  }
}
