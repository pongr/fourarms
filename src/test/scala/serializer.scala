package com.pongr.fourarms.serializer

import org.specs2.mutable._
import org.apache.mailet.Mail
import org.apache.james.core.MailImpl
import org.apache.commons.io.IOUtils

import java.util.Properties
import javax.mail._
import javax.mail.internet._
import java.io._

import com.pongr.fourarms.mail.PongrMail

class SerializerSpec extends Specification {

  def serializer = new DefaultSerializer

  def createMimeMessage = {
    val message = new MimeMessage(null: Session)

    val sender = new InternetAddress("test-sender@pongr.com", "Test Sender")
    val recipient = new InternetAddress("test-recipient@pongr.com", "Test Recipient")
    val cc = new InternetAddress("test-cc@pongr.com", "TEST CC")

    message.setFrom(sender)
    message.setRecipient(Message.RecipientType.TO, recipient)
    message.setRecipient(Message.RecipientType.CC, cc)
    message.setSubject("Test mail subject")

    val multipart = new MimeMultipart

    // Part one
    val messageBodyPart = new MimeBodyPart
    messageBodyPart.setText("Hi, This is test email body.")
    multipart.addBodyPart(messageBodyPart)

    // Part two => attachment
    val stream = getClass.getResourceAsStream("/test.jpg")
    multipart.addBodyPart(new MimeBodyPart(stream))

    // Put parts in message
    message.setContent(multipart)

    message
  }

  def isEqual(m1: PongrMail, m2: PongrMail): Boolean = {

    val i1 = m1.message.getContent.asInstanceOf[MimeMultipart].getBodyPart(1).getInputStream
    val i2 = m2.message.getContent.asInstanceOf[MimeMultipart].getBodyPart(1).getInputStream
    val txt1 = m1.message.getContent.asInstanceOf[MimeMultipart].getBodyPart(0).getContent
    val txt2 = m2.message.getContent.asInstanceOf[MimeMultipart].getBodyPart(0).getContent

    m1.remoteHost                        == m2.remoteHost &&
    m1.remoteAddr                        == m2.remoteAddr && 
    m1.sender                            == m2.sender &&
    m1.recipients                        == m2.recipients &&
    m1.message.getSubject                == m2.message.getSubject &&
    m1.message.getFrom.toList            == m2.message.getFrom.toList &&
    m1.message.getAllRecipients.toList   == m2.message.getAllRecipients.toList &&
    txt1 == txt2 &&
    IOUtils.contentEquals(i1, i2)
  }

  "SerializerSpec test" should {

    "serialize/deserialize mail object" in {

      val mail = new MailImpl
      mail.setMessage(createMimeMessage)
      val m1 = PongrMail(mail)

      val bytes = serializer.serialize(m1)
      val m2 = serializer.deserialize(bytes)

      isEqual(m1, m2) must_== true

    }

  }

}
