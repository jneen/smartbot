package test

import org.specs._
import smartbot._

class SmartBotSpec extends Specification {

  "shouldReturn" should {

    val bot = new SmartBot.Bot(
      name = "botName",
      dict = MarkovDict.empty(3),
      logPath = "examples/irc_log.log",
      responseFrequency = 1000,
      rateLimitMax = 10
    )

    "return true if their name was in the message" in {
      bot.shouldRespond("sender", "hi botName") must be_==(true)
    }

    "return false for an arbitary message" in {
      bot.shouldRespond("sender", "blah blah") must be_==(false)
    }
  }

}
