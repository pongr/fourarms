package com.pongr.fourarms.mail

import org.apache.mailet._
import javax.mail.internet._

object PongrMail {

  def apply(m: Mail): PongrMail = 
    PongrMail(m.getSender, m.getRecipients, m.getRemoteHost, m.getRemoteAddr, m.getMessage)

}

case class PongrMail(sender: MailAddress, 
                     recipients: java.util.Collection[_],
                     remoteHost: String,
                     remoteAddr: String,
                     message: MimeMessage
                    )
