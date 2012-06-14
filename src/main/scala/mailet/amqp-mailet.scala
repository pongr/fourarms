package com.pongr.fourarms.mailet

import org.apache.mailet._
import org.apache.mailet.base._
import com.rabbitmq.client._
import org.apache.commons.lang.StringUtils.isBlank


import com.pongr.fourarms.util._
import com.pongr.fourarms.serializer._

class AmqpMailet extends PongrMailet with FromMethods {

  override def service(mail: Mail) {

    val serializerName = getInitParameter("serializer").trim
    val host = getInitParameter("host")
    val port = getInitParameter("port")
    val username = getInitParameter("username")
    val password = getInitParameter("password")
    val vhost = getInitParameter("vhost")
    val exchange = getInitParameter("exchange")
    val queue = getInitParameter("queue")
    val routingKey = getInitParameter("routing-key")
    val exchangeType = getInitParameter("exchangeType", "direct")

    val setGhostState_? = getInitParameter("ghost", true)

    val uri = "amqp://%s:%s@%s:%s/%s" format (username, password, host, port, vhost)
    val serializer = if (isBlank(serializerName))
                       new DefaultSerializer 
                     else
                       Class.forName(serializerName).newInstance().asInstanceOf[Serializer]

    // serialize
    val bytes = serializer.serialize(mail)

    val factory = new ConnectionFactory()
    factory.setUri(uri)
    val conn = factory.newConnection()
    val channel = conn.createChannel()

    // a durable exchange
    channel.exchangeDeclare(exchange, exchangeType, true)

    // a durable, non-exclusive, non-autodelete queue
    channel.queueDeclare(queue, true, false, false, null)

    channel.queueBind(queue, exchange, routingKey)

    log("Sending (From: %s, Name: %s) to AMQP queue." format (getFromEmail(mail), mail.getName))
    channel.basicPublish(exchange, routingKey, null, bytes)

    channel.close()
    conn.close()

    if (setGhostState_?)
      mail.setState(Mail.GHOST)

  }

}
