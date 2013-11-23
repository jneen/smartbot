package smartbot

import org.jibble.pircbot._
import LogParser._


object SmartBot {

  class Bot(var name: String, dict: MarkovDict, logPath: String) extends PircBot {

    def connect(server: String, channel: String, passwordOpt: Option[String]) {
      try {
        setName(name)
        setEncoding("UTF-8")
        setVerbose(true)
        connect(server)
        joinChannel(channel)
      } catch {
        case e: NickAlreadyInUseException => {
          name = name + "t"
          println("connecting to the server as `"+name+"'")
          connect(server, channel, passwordOpt)
        }
      }
    }

    override def onMessage(channel: String, sender: String, login: String,
                           hostname: String, message: String) {
      val reply = removePings(channel, dict.generateSentence())
      sendMessage(channel, reply)

      dict.train(message)
      addToLog(logPath, message)
    }

    override def onPrivateMessage(sender: String, login: String,
                                  hostname: String, message: String) {
      sendMessage(sender, dict.generateSentence())
    }

    private def removePings(channel: String, message: String) : String = {
      getUsers(channel).foldLeft(message)({ (replaced, user) =>
        val nick = user.getNick()
        val mangled = nick.head + "." + nick.tail
        replaced.replaceAll(nick, mangled)
      })
    }
  }

  def main(args: Array[String]) {
    val botName = sys.env.get("BOT_NAME").getOrElse("stufflebot")
    val channel = sys.env.get("CHANNEL").getOrElse("#csuatest")
    val server = sys.env.get("SERVER").getOrElse("irc.freenode.net")
    val logPath = sys.env.get("LOG_PATH").getOrElse("./irc_logs/csua.log")
    val passwordOpt = sys.env.get("PASSWORD")

    val dict = MarkovDict.trainFromLog(logPath)
    println("finished training the bot")
    val bot = new Bot(botName, dict, logPath)

    bot.connect(server, channel, passwordOpt)
  }
}
