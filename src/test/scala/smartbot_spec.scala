package test

import org.specs._
import smartbot._

class SmartbotSpec extends Specification {
  "Smartbot" should {
    "create an empty one" in {
      val empty = Smartbot.MarkovDict.empty()
      empty.links size should be_==(0)
    }
  }
}
