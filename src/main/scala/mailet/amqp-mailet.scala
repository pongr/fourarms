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
  // lazy val queue = getInitParameter("queue")
  lazy val routingKey = getInitParameter("routing-key")
  lazy val exchangeType = getInitParameter("exchangeType", "direct")

  lazy val setGhostState_? = getInitParameter("ghost", true)

  lazy val serializer = if (isBlank(serializerName))
                          new DefaultSerializer 
                        else
                          Class.forName(serializerName).newInstance().asInstanceOf[Serializer]
  var conn : Connection = _

  override def init() {
    val uri = "amqp://%s:%s@%s:%s/%s" format (username, password, host, port, vhost)
    val factory = new ConnectionFactory()
    factory.setUri(uri)
    conn = factory.newConnection()
    
    val channel = conn.createChannel()
    // a durable exchange
    channel.exchangeDeclare(exchange, exchangeType, true)
    channel.close()
  }

  override def service(mail: Mail) {

    // serialize
    val bytes = serializer.serialize(mail)

    val channel = conn.createChannel()

    // a durable, non-exclusive, non-autodelete queue
    // channel.queueDeclare(queue, true, false, false, null)
    // channel.queueBind(queue, exchange, routingKey)

    log("Sending (From: %s, Name: %s) to AMQP(VHost: %s, exchange: %s)." format (getFromEmail(mail), mail.getName, vhost, exchange))
    channel.basicPublish(exchange, routingKey, null, bytes)

    channel.close()

    if (setGhostState_?)
      mail.setState(Mail.GHOST)

  }

  override def destroy() {
    conn.close()
  }

}
