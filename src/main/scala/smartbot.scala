package smartbot

import org.jibble.pircbot.PircBot


object SmartBot extends PircBot {

  def main(args: Array[String]) {
    setName("stufflebotttt")
    setVerbose(true)
    setEncoding("UTF-8")
    connect("irc.freenode.net")
    identify("******")
    joinChannel("#csuatest")
  }

  override def onMessage(channel: String, sender: String, login:
			 String, hostname: String, message: String) {
    sendMessage(channel, "testing")
  }
}
