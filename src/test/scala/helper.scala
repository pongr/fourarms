package com.pongr.fourarms

import org.apache.mailet.MailAddress
import org.apache.james.core.MailImpl
import org.apache.commons.io.IOUtils

import javax.mail._
import javax.mail.internet._
import java.io._
import scala.collection.JavaConversions._

trait Helper {

  def mail1 = createMail("/MimeMessage-plain-jpeg-multipleRecipient")
  def mail2 = createMail("/MimeMessage-plain")
  def mail3 = createMail("/MimeMessage-plain-jpeg")
  def mail4 = createMail("/MimeMessage-plain-html")

  def createMail(fileName: String) = {
    val stream = getClass.getResourceAsStream(fileName)
    val bis = new ByteArrayInputStream(IOUtils.toByteArray(stream))
    val message = new MimeMessage(null, bis)

    val r = message.getRecipients(Message.RecipientType.TO)
    val recipients: java.util.Collection[_] = r.map(a => new MailAddress(a.toString)).toList
    val sender = new MailAddress(message.getFrom.head.asInstanceOf[InternetAddress])
    new MailImpl(message.toString, sender, recipients, message)
  }

}
