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

import com.pongr.fourarms.mail._
import org.apache.mailet._
import grizzled.slf4j.Logging
import java.io._
import javax.mail.internet._

/** Converts Email to Array[Byte] */
trait Serializer {

  def serialize(m: Email): Array[Byte]
  def serialize(m: Mail): Array[Byte] = serialize(Email(m))

}

/** Converts Array[Byte] to Email */
trait Deserializer {

  def deserialize(b: Array[Byte]): Email

}

/** 
  * Default implementation of Serializer and Deserializer traits.
  */
class DefaultSerializer extends Serializer with Deserializer with Logging {
  def serialize(mail: Email): Array[Byte] = {
    val bos = new ByteArrayOutputStream
    val out = new ObjectOutputStream(bos)

    info("Serializing email object. Subject: " + mail.subject)
    out.writeObject(mail)

    out.close
    bos.toByteArray
  }

  def deserialize(b: Array[Byte]): Email = {
    val bis = new ByteArrayInputStream(b)
    val ois = new ObjectInputStream(bis)

    val sender = ois.readObject.asInstanceOf[Email]
    ois.close
    sender
  }

}
