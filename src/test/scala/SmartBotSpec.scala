package test

import org.specs._
import smartbot._

class SmartBotSpec extends Specification {

  "getSeed" should {

    val bot = new SmartBot.Bot(
      name = "botName",
      dict = MarkovDict.empty(3),
      logPath = "examples/irc_log.log",
      catchphrases = Array(Array("foo", "bar")),
      responseFrequency = 1000,
      rateLimitMax = 10
    )

    "seed with a catchphrase" in {
      bot.getSeed("foo bar baz") must be_==(Some("foo bar"))
    }

    "seed with a ping" in {
      bot.getSeed("botName: foo bar baz") must be_==(Some("foo bar baz"))
    }

    "return None for an arbitary message" in {
      bot.getSeed("blah blah") must be_==(None)
    }
  }

}
