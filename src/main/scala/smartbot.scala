package smartbot

import org.jibble.pircbot._
import LogParser._
import scala.util.Random


object SmartBot {

  class Bot(var name: String,
            dict: MarkovDict,
            logPath: String,
            responseFrequency: Int,
            writeToLog: Boolean = false,
            rateLimitMax: Int = 10,
            rateLimitInterval: Int = 600) extends PircBot {
    var lastInterval: Long = 0
    var rateLimitCount = 0
    var hasReplied = false

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
      wantToRespondTo.exists(message.contains(_))
    }

    def getSeed(message: String) : Option[String] = {
      val idx = message.lastIndexOfSlice(name)

      if (idx > 0) {
        Some(message.take(idx).trim())
      }
      else {
        None
      }
    }

    override def onMessage(channel: String, sender: String, login: String,
                           hostname: String, message: String) {
      if (shouldRespond(sender, message) || Random.nextInt(responseFrequency) == 0) {
        val sentence = getSeed(message) match {
          case Some(seed) => dict.generateSentence(seed)
          case _ => dict.generateSentence()
        }

        if (rateLimit()) {
          sendSanitized(channel, sentence)
        }
        else if (!hasReplied) {
          sendMessage(channel, "nope.gif (I'm rate limiting my replies, so I don't spam this channel)")
          hasReplied = true
        }
      }
      else if (writeToLog && !sender.contains("bot")) {
        dict.train(message)
        addToLog(logPath, message)
      }
    }

    override def onPrivateMessage(sender: String, login: String,
                                  hostname: String, message: String) {
      sendMessage(sender, dict.generateSentence())
    }

    private def sanitizeMessage(channel: String, message: String) {
      removeBang(removePings(channel, message))
    }

    private def sendSanitized(channel: String, message: String) {
      sendMessage(channel, removeBang(removePings(channel, message)))
    }

    private def removeBang(message: String) : String = {
      if (message.startsWith("!")) {
        "."+message
      }
      else {
        message
      }
    }

    private def removePings(channel: String, message: String) : String = {
      getUsers(channel).foldLeft(message)({ (replaced, user) =>
        val nick = user.getNick()
        val mangled = nick.head + "." + nick.tail
        replaced.replaceAllLiterally(nick, mangled)
      })
    }

    private def rateLimit(): Boolean = {
      val now = System.currentTimeMillis / 1000
      val currentInterval = now / rateLimitInterval

      if (currentInterval == lastInterval) {
        rateLimitCount += 1
      }
      else {
        lastInterval = currentInterval
        hasReplied = false
        rateLimitCount = 0
      }

      rateLimitCount < rateLimitMax
    }
  }

  def main(args: Array[String]) {
    val botName = sys.env.get("BOT_NAME").getOrElse("artemisphd")
    val channel = sys.env.get("CHANNEL").getOrElse("#csuatest")
    val server = sys.env.get("SERVER").getOrElse("irc.freenode.net")
    val logPath = sys.env.get("LOG_PATH").getOrElse("./irc_logs/csua.log")
    val responseRatio = sys.env.get("RESPONSE_RATIO").getOrElse("80").toInt
    val rateLimit = sys.env.get("RATE_LIMIT").getOrElse("10").toInt
    val readOnly = sys.env.get("READ_ONLY").getOrElse("0")
    val passwordOpt = sys.env.get("PASSWORD")

    val dict = MarkovDict.trainFromLog(logPath)
    println("finished training the bot")

    val bot = new Bot(
      name = botName,
      dict = dict,
      logPath = logPath,
      responseFrequency = responseRatio,
      writeToLog = (readOnly == "0"),
      rateLimitMax = rateLimit
    )

    bot.connect(server, channel, passwordOpt)
  }
}
