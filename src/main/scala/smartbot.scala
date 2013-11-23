package smartbot

import org.jibble.pircbot._


object SmartBot extends PircBot {

  var name = "markovbot"

  def main(args: Array[String]) {
    connect("irc.freenode.net", "#csuatest")
  }

  def connect(server: String, channel:String) {
    try {
      setName(name)
      setEncoding("UTF-8")
      connect(server)
      joinChannel(channel) 
    } catch { 
      case e: NickAlreadyInUseException => {
	name = name + "t"
	connect("irc.freenode.net", "#csuatest")
      }
    }
  }

  override def onMessage(channel: String, sender: String, login:
			 String, hostname: String, message: String) {
    sendMessage(channel, "testing")
  }
}
