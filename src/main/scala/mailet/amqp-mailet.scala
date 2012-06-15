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

package com.pongr.fourarms.mailet

import org.apache.mailet._
import org.apache.mailet.base._
import com.rabbitmq.client._
import org.apache.commons.lang.StringUtils.isBlank

import com.pongr.fourarms.util._
import com.pongr.fourarms.serializer._


/**
 * AMQP mailet serializes incoming emails and sends to AMQP server. 
 */
class AmqpMailet extends PongrMailet with FromMethods {

  lazy val serializerName = getInitParameter("serializer").trim

  /** AMQP server host */
  lazy val host = getInitParameter("host")

  /** AMQP server port */
  lazy val port = getInitParameter("port")

  /** username */
  lazy val username = getInitParameter("username")

  /** password */
  lazy val password = getInitParameter("password")

  /** AMQP virtual host */
  lazy val vhost = getInitParameter("vhost")

  /** AMQP exchange name. */
  lazy val exchange = getInitParameter("exchange")

  /** AMQP routing key */
  lazy val routingKey = getInitParameter("routing-key")

  /** AMQP exchange type */
  lazy val exchangeType = getInitParameter("exchangeType", "direct")

  /** AMQP boolean value to define wheter to set email state to GHOST or not after it's been sent. */
  lazy val setGhostState_? = getInitParameter("ghost", true)

  /** If it's provided by the parameter creates an instance of that using Reflection.
    * Otherwise use the default Serializer. 
    */
  lazy val serializer = if (isBlank(serializerName))
                          new DefaultSerializer 
                        else
                          Class.forName(serializerName).newInstance().asInstanceOf[Serializer]
                          
  /** URI, where AMQP credentials will be validated. */
  lazy val uri = "amqp://%s:%s@%s:%s/%s" format (username, password, host, port, vhost)
  var conn : Connection = _

  /** Creates amqp connection and declares channel */
  override def init() {
    log("Opening AMQP Connection to %s..." format uri)
    val factory = new ConnectionFactory()
    factory.setUri(uri)
    conn = factory.newConnection()
    log("Opened AMQP Connection to %s" format uri)
    
    val channel = conn.createChannel()
    channel.exchangeDeclare(exchange, exchangeType, true)
    log("Declared durable %s exchange %s" format (exchangeType, exchange))
    channel.close()
  }

  /** Serializes email and send to AMQP server */
  override def service(mail: Mail) {

    // serialize 
    val bytes = serializer.serialize(mail)
    
    import java.io._
    import org.apache.commons.io._
    val file = File.createTempFile("serializedMail", "")
    FileUtils.writeByteArrayToFile(file, bytes)
    log("Saved bytes to %s" format file)

    val channel = conn.createChannel()

    log("Sending (From: %s, Name: %s) to AMQP(VHost: %s, exchange: %s)." format (getFromEmail(mail), mail.getName, vhost, exchange))
    channel.basicPublish(exchange, routingKey, null, bytes)

    channel.close()

    if (setGhostState_?)
      mail.setState(Mail.GHOST)

  }

  /** Close the AMQP connection */
  override def destroy() {
    conn.close()
    log("Closed AMQP Connection to %s" format uri)
  }

}
