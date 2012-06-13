package com.pongr.fourarms.mailet

import org.apache.mailet._
import scala.collection.JavaConversions._

class ChangeRecipient extends PongrMailet {
  lazy val oldRecipient = getInitParameter("oldRecipient").trim.toLowerCase
  lazy val newRecipient = getInitParameter("newRecipient").trim.toLowerCase

  override def service(mail: Mail) {
    val rs = mail.getRecipients.toList.asInstanceOf[List[MailAddress]]
    val rs2 = rs map { r =>
      if (oldRecipient == r.toString.trim.toLowerCase) {
        log("Changed " + r + " to " + newRecipient)
          new MailAddress(newRecipient)
      } else
        r
    }
    mail.setRecipients(rs2)
  }
}

