package test

import org.specs._
import smartbot._

class SmartbotSpec extends Specification {
  "Smartbot" should {
    "fail" in {
      0 should be_==(1)
    }
  }

  SmartBot.main()
}
