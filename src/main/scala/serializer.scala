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

class JavaNativeSerializer extends Serializer with Deserializer {

  def serialize(mail: Mail): Array[Byte] = {
    val bos = new ByteArrayOutputStream
    val out = new ObjectOutputStream(bos)
    out.writeObject(mail)
    mail.getMessage.writeTo(bos)
    out.close
    bos.toByteArray
  }

  def deserialize(b: Array[Byte]): Mail = {
    val bis = new ByteArrayInputStream(b)
    val ois = new ObjectInputStream(bis)
    val mail = ois.readObject.asInstanceOf[Mail]
    val message = new MimeMessage(null, bis)
    ois.close
    mail.setMessage(message)
    mail
  }

}
