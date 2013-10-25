package test

import org.specs._
import smartbot._

class SmartbotSpec extends Specification {
  "Smartbot" should {
    "create an empty one" in {
      val empty = Smartbot.MarkovDict.empty()
      empty.links.size must be_==(0)
    }

    "you must use must" in {
      1 must be_==(1)
    }
  }
}
