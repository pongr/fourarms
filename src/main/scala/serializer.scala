package com.pongr.fourarms.serializer

import org.apache.mailet._

import java.io._
import javax.mail.internet.MimeMessage

trait Serializer {

  def serialize(m: Mail): Array[Byte]

}

trait Deserializer {

  def deserialize(b: Array[Byte]): Mail

}

class DefaultSerializer extends Serializer with Deserializer {

  def serialize(mail: Mail): Array[Byte] = {
    val mailBos = new ByteArrayOutputStream
    val out = new ObjectOutputStream(mailBos)
    out.writeObject(mail)
    out.close

    val mimeBos = new ByteArrayOutputStream
    mail.getMessage.writeTo(mimeBos)

    val combined = Array(mailBos.toByteArray, mimeBos.toByteArray)

    val combinedBos = new ByteArrayOutputStream
    val combinedOut = new ObjectOutputStream(combinedBos)
    combinedOut.writeObject(combined)
    combinedOut.close

    combinedBos.toByteArray
  }

  def deserialize(b: Array[Byte]): Mail = {
    val combinedBis = new ByteArrayInputStream(b)
    val combinedOis = new ObjectInputStream(combinedBis)
    val combined = combinedOis.readObject.asInstanceOf[Array[Array[Byte]]]

    val mailBis = new ByteArrayInputStream(combined(0))
    val mailOis = new ObjectInputStream(mailBis)
    val mail = mailOis.readObject.asInstanceOf[Mail]

    val message = new MimeMessage(null, new ByteArrayInputStream(combined(1)))

    combinedOis.close
    mail.setMessage(message)
    mail
  }

}
