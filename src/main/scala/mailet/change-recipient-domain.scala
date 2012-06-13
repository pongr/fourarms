package com.pongr.fourarms.mailet

import org.apache.mailet._
import scala.collection.JavaConversions._

class ChangeRecipientDomain extends PongrMailet {
  lazy val oldDomain = getInitParameter("oldDomain").trim.toLowerCase
  lazy val newDomain = getInitParameter("newDomain").trim.toLowerCase

  override def service(mail: Mail) {
    val rs = mail.getRecipients.toList.asInstanceOf[List[MailAddress]]
    val rs2 = rs map { r =>
      if (oldDomain == r.getDomain) {
        val r2 = new MailAddress(r.getLocalPart, newDomain)
        log("Changed " + r +  " to " + r2)
        r2
      } else
        r
    }
    mail.setRecipients(rs2)
  }

}

