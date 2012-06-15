/*
 * Copyright (c) 2012 Pongr, Inc.
 *   
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pongr.fourarms.serializer

import com.pongr.fourarms.mail.PongrMail
import org.apache.mailet._

import java.io._
import javax.mail.internet._

/** Converts PongrMail to Array[Byte] */
trait Serializer {

  def serialize(m: PongrMail): Array[Byte]
  def serialize(m: Mail): Array[Byte] = serialize(PongrMail(m))

}

/** Converts Array[Byte] to PongrMail */
trait Deserializer {

  def deserialize(b: Array[Byte]): PongrMail

}

/** 
  * Default implementation of Serialize and Deserializer traits.
  */
class DefaultSerializer extends Serializer with Deserializer {
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
