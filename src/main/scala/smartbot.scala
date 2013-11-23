package smartbot

import org.jibble.pircbot._


object SmartBot {
  class Bot(var name: String, dict: MarkovDict, dbPath: String) extends PircBot {
    def setup(name: String) {
      setEncoding("UTF-8")
      setVerbose(true)
    }

    def connect(server: String, channel: String, passwordOpt: Option[String]) {
      try {
        setName(name)
        setEncoding("UTF-8")
        connect(server)
        joinChannel(channel) 
      } catch {
        case e: NickAlreadyInUseException => {
          name = name + "t"
          connect(server, channel, passwordOpt)
        }
      }
    }

    override def onMessage(channel: String, sender: String, login: String,
                           hostname: String, message: String) {

      sendMessage(channel, dict.generateSentence(message))
    }
  }

  def main(args: Array[String]) {
    val botName = sys.env.get("BOT_NAME").getOrElse("yabishbot")
    val channel = sys.env.get("CHANNEL").getOrElse("#csuatest")
    val server = sys.env.get("SERVER").getOrElse("irc.freenode.net")
    val dbPath = sys.env.get("DB_PATH").getOrElse("./db/ya_bish")
    val passwordOpt = sys.env.get("PASSWORD")

    val dict = MarkovDict.trainFromLog(dbPath)
    val bot = new Bot(botName, dict, dbPath)

    bot.connect(server, channel, passwordOpt)
  }
}
