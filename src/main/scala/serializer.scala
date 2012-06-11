package com.pongr.fourarms.serializer

import org.apache.mailet._

import java.io._

trait Serializer {

  def serialize(m: Mail): Array[Byte]

}

trait Deserializer {

  def deSerialize(b: Array[Byte]): Mail

}

trait JavaSerializer extends Serializer with Deserializer {

  def serialize(mail: Mail): Array[Byte] = {
    val bos = new ByteArrayOutputStream
    val out = new ObjectOutputStream(bos)
    out.writeObject(mail)
    out.close

    bos.toByteArray
  }

  def deSerialize(b: Array[Byte]): Mail = {
    val stream = new ByteArrayInputStream(b)
    val in = new ObjectInputStream(stream)
    val mail = in.readObject.asInstanceOf[Mail]
    in.close
    mail
  }

}
