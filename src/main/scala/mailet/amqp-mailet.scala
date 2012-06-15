package com.pongr.fourarms.mailet

import org.apache.mailet._
import org.apache.mailet.base._
import com.rabbitmq.client._
import org.apache.commons.lang.StringUtils.isBlank


import com.pongr.fourarms.util._
import com.pongr.fourarms.serializer._

class AmqpMailet extends PongrMailet with FromMethods {

  lazy val serializerName = getInitParameter("serializer").trim
  lazy val host = getInitParameter("host")
  lazy val port = getInitParameter("port")
  lazy val username = getInitParameter("username")
  lazy val password = getInitParameter("password")
  lazy val vhost = getInitParameter("vhost")
  lazy val exchange = getInitParameter("exchange")
  lazy val routingKey = getInitParameter("routing-key")
  lazy val exchangeType = getInitParameter("exchangeType", "direct")

  lazy val setGhostState_? = getInitParameter("ghost", true)

  lazy val serializer = if (isBlank(serializerName))
                          new DefaultSerializer 
                        else
                          Class.forName(serializerName).newInstance().asInstanceOf[Serializer]
                          
  lazy val uri = "amqp://%s:%s@%s:%s/%s" format (username, password, host, port, vhost)
  var conn : Connection = _

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

  override def destroy() {
    conn.close()
    log("Closed AMQP Connection to %s" format uri)
  }

}
