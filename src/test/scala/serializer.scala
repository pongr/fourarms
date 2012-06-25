package com.pongr.fourarms.serializer

import org.specs2.mutable._
import org.apache.mailet.{ Mail, MailAddress }
import org.apache.james.core.MailImpl
import org.apache.commons.io.IOUtils
import org.apache.mailet.base.RFC2822Headers

import java.util.Properties
import javax.mail._
import javax.mail.internet._
import java.io._
import scala.collection.JavaConversions._

import com.pongr.fourarms.mail._

class SerializerSpec extends Specification {

  def serializer = new DefaultSerializer

  def deserializeMimeMessage(b: Array[Byte]): MimeMessage = {
    val bis = new ByteArrayInputStream(b)
    new MimeMessage(null, bis)
  }

  def createMail(message: MimeMessage) = {
    val r = message.getRecipients(Message.RecipientType.TO)
    val recipients: java.util.Collection[_] = r.map(a => new MailAddress(a.toString)).toList
    val sender = new MailAddress(message.getFrom.head.asInstanceOf[InternetAddress])
    new MailImpl(message.toString, sender, recipients, message)
  }

  def getEmail(fileName: String) = {
    val stream = getClass.getResourceAsStream(fileName)
    val message = deserializeMimeMessage(IOUtils.toByteArray(stream))
    Email(createMail(message))
  }

  def isEqual(m1: Email, m2: Email): Boolean = {
    /*println(m1.from          == m2.from )*/
    /*println(m1.to            == m2.to )*/
    /*println(m1.subject       == m2.subject )*/
    /*println(m1.parts         == m2.parts )*/
    /*println(m1.headers       == m2.headers )*/
    /*println(m1.remoteHost    == m2.remoteHost )*/
    /*println(m1.remoteAddr    == m2.remoteAddr)*/
    /*println(m1.parts)*/
    /*println(m2.parts)*/

    m1.from          == m2.from &&
    m1.to            == m2.to &&
    m1.subject       == m2.subject &&
    m1.parts         == m2.parts &&
    m1.headers       == m2.headers &&
    m1.remoteHost    == m2.remoteHost &&
    m1.remoteAddr    == m2.remoteAddr
  }

  "SerializerSpec test" should {

    "serialize/deserialize mail object" in {

      val m1 = getEmail("/MimeMessage-plain-jpeg-multipleRecipient")

      println(m1)

      val bytes = serializer.serialize(m1)
      val m2 = serializer.deserialize(bytes)


      isEqual(m1, m2) must_== true

    }

  }

}
