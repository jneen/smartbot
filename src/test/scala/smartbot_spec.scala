package test

import org.specs._
import smartbot._

class SmartbotSpec extends Specification {
  "Smartbot" should {
    "fail" in {
      1 must be_==(1)
    }
  }

}
