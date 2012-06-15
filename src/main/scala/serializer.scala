package com.pongr.fourarms.serializer

import com.pongr.fourarms.mail.PongrMail
import org.apache.mailet._

import java.io._
import javax.mail.internet._

trait Serializer {

  def serialize(m: PongrMail): Array[Byte]
  def serialize(m: Mail): Array[Byte] = serialize(PongrMail(m))

}

trait Deserializer {

  def deserialize(b: Array[Byte]): PongrMail

}

class DefaultSerializer extends Serializer with Deserializer {
  /** Serializes emails
    * 
    */
  def serialize(mail: PongrMail): Array[Byte] = {
    val bos = new ByteArrayOutputStream
    val out = new ObjectOutputStream(bos)

    out.writeObject(mail.sender)
    out.writeObject(mail.recipients)
    out.writeObject(mail.remoteHost)
    out.writeObject(mail.remoteAddr)

    mail.message.writeTo(bos)

    out.close
    bos.toByteArray
  }

  def deserialize(b: Array[Byte]): PongrMail = {
    val bis = new ByteArrayInputStream(b)
    val ois = new ObjectInputStream(bis)

    val sender = ois.readObject.asInstanceOf[MailAddress]
    val recipients = ois.readObject.asInstanceOf[java.util.Collection[_]]
    val remoteHost = ois.readObject.asInstanceOf[String]
    val remoteAddr = ois.readObject.asInstanceOf[String]

    val message = new MimeMessage(null, bis)
    ois.close

    PongrMail(sender, recipients, remoteHost, remoteAddr, message)
  }

}
