package smartbot

import org.jibble.pircbot._
import LogParser._
import scala.util.Random


object SmartBot {

  class Bot(var name: String, dict: MarkovDict, logPath: String, responseFrequency: Int) extends PircBot {

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

    def shouldRespond(sender: String, message: String): Boolean = {
      val wantToRespondTo = List(name)
      val words = dict.tokenize(message)
      wantToRespondTo.map(words.contains(_)).foldLeft(false) {_ || _}
    }

    override def onMessage(channel: String, sender: String, login: String,
                           hostname: String, message: String) {
      if (shouldRespond(sender, message) || Random.nextInt(responseFrequency) == 0) {
        val reply = removePings(channel, dict.generateSentence())
        sendMessage(channel, reply)
      }
      if (!sender.contains("bot")) {
        dict.train(message)
        addToLog(logPath, message)
      }
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
    val responseRatio = sys.env.get("RESPONSE_RATIO").getOrElse("80").toInt
    val passwordOpt = sys.env.get("PASSWORD")

    val dict = MarkovDict.trainFromLog(logPath)
    println("finished training the bot")

    val bot = new Bot(botName, dict, logPath, responseRatio)
    bot.connect(server, channel, passwordOpt)
  }
}
