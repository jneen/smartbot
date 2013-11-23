package smartbot

import scala.io.Source


object LogParser {

  def getDepth(s: String): Option[Int] = {
    /** If the string is a number, returns the option of it,
     *  otherwise it returns None */
    val IntRegEx = "(\\d+)".r
    s match {
      case IntRegEx(num) => Some(num.toInt)
      case _ => None
    }
  }

  def getMessages(file: String): (Option[Int], Iterator[String]) = {
    val messages = Source.fromFile(file).getLines
    val firstLine = messages.next()
    (getDepth(firstLine), messages)
  }

}
