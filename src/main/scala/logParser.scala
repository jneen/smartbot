package smartbot

import scala.io.Source


object LogParser {

  def getMessages(file: String): Iterator[String] = { 
    Source.fromFile(file).getLines
  }

}
