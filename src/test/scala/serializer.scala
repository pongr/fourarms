package com.pongr.fourarms.serializer

import org.specs2.mutable._
import org.apache.mailet.Mail
import org.apache.james.core.MailImpl
import org.apache.commons.io.IOUtils

import java.util.Properties
import javax.mail._
import javax.mail.internet._
import java.io._

import org.apache.mailet.base.test._

class SerializerSpec extends Specification {

  def serializer = new JavaNativeSerializer

  def createMimeMessage = {
    val message = new MimeMessage(null: Session)

    val sender = new InternetAddress("test-sender@pongr.com", "Test Sender")
    val recipient = new InternetAddress("test-recipient@pongr.com", "Test Recipient")

    message.setFrom(sender)
    message.setRecipient(Message.RecipientType.TO, recipient)
    message.setSubject("Test mail subject")

    val multipart = new MimeMultipart

    // Part one
    val messageBodyPart = new MimeBodyPart
    messageBodyPart.setText("Hi");
    multipart.addBodyPart(messageBodyPart)

    // Part two => attachment
    val stream = getClass.getResourceAsStream("/test.jpg")
    multipart.addBodyPart(new MimeBodyPart(stream))

    // Put parts in message
    message.setContent(multipart)

    message
  }

  def isEqual(m1: Mail, m2: Mail): Boolean = {

    println(m1.getName == m2.getName)
    println(m1.getRemoteHost == m2.getRemoteHost )
    println(m1.getRemoteAddr == m2.getRemoteAddr ) 
    println(m1.getRecipients == m2.getRecipients )
    println(m1.getSender == m2.getSender )
    println(m1.getMessage.getSubject == m2.getMessage.getSubject)
    println(m1.getMessage.getFrom.toList == m2.getMessage.getFrom.toList )
    println(m1.getMessage.getContent == m2.getMessage.getContent)
    println(m1.getMessage == m2.getMessage)

    println(m1.getMessage.getContent.asInstanceOf[MimeMultipart].getBodyPart(0))
    println(m1.getMessage.getContent.asInstanceOf[MimeMultipart].getBodyPart(1))
    println(m2.getMessage.getContent.asInstanceOf[MimeMultipart].getBodyPart(0))
    println(m2.getMessage.getContent.asInstanceOf[MimeMultipart].getBodyPart(1))

    val i1 = m1.getMessage.getContent.asInstanceOf[MimeMultipart].getBodyPart(1).getInputStream
    val i2 = m2.getMessage.getContent.asInstanceOf[MimeMultipart].getBodyPart(1).getInputStream

    val (b1, b2) = (IOUtils.toByteArray(i1), IOUtils.toByteArray(i2))
    println(i1 == i2)
    println(b1.toList == b2.toList)
    println(IOUtils.contentEquals(i1, i2))

    m1.getName                       == m2.getName &&
    m1.getRemoteHost                 == m2.getRemoteHost &&
    m1.getRemoteAddr                 == m2.getRemoteAddr && 
    m1.getSender                     == m2.getSender &&
    m1.getMessage.getSubject         == m2.getMessage.getSubject &&
    m1.getRecipients                 == m2.getRecipients &&
    m1.getMessage.getFrom.toList     == m2.getMessage.getFrom.toList &&
    m1.getMessage.getContent         == m2.getMessage.getContent
  }

  "SerializerSpec test" should {

    "serialize mail object" in {

      val message = createMimeMessage
      val m1 = new MailImpl()
      m1.setMessage(message)

      val bytes = serializer.serialize(m1)

      val m2 = serializer.deserialize(bytes)

      isEqual(m1, m2) must_== true

    }

  }

}
