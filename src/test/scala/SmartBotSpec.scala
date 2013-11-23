package test

import org.specs._
import smartbot._

class SmartBotSpec extends Specification {

  "shouldReturn" should {

    val bot = new SmartBot.Bot("botName", MarkovDict.empty(3), "examples/irc_log.log", 1000)

    "return true if their name was in the message" in {
      bot.shouldRespond("sender", "hi botName") must be_==(true)
    }

    "return false for an arbitary message" in {
      bot.shouldRespond("sender", "blah blah") must be_==(false)
    }
  }

}
