package com.pongr.fourarms.mailet

import org.apache.mailet._
import org.apache.mailet.base._
import com.rabbitmq.client._

import com.pongr.fourarms.serializer._

class AmqpMailet extends GenericMailet {

  override def service(mail: Mail) {

    val serializerName = getInitParameter("serializer").trim
    val host = getInitParameter("host")
    val port = getInitParameter("port")
    val username = getInitParameter("username")
    val password = getInitParameter("password")
    val vhost = getInitParameter("vhost")
    val exchange = getInitParameter("exchange")
    val queue = getInitParameter("queue")
    val key = getInitParameter("key")
    val protocol = getInitParameter("protocol")

    val uri = "amqp://%s:%s@%s:%s/%s" format (username, password, host, port, vhost)
    val serializer = if (serializerName.trim == "")
                       new JavaNativeSerializer 
                     else
                       Class.forName(serializerName).newInstance().asInstanceOf[Serializer]

    // serialize
    val bytes = serializer.serialize(mail)

    val factory = new ConnectionFactory()
    factory.setUri(uri)
    val conn = factory.newConnection()

    val channel = conn.createChannel()

    channel.exchangeDeclare(exchange, protocol, true)
    channel.queueDeclare(queue, true, false, false, null)
    channel.queueBind(queue, exchange, key)

    channel.basicPublish(exchange, key, null, bytes)

    channel.close()
    conn.close()

  }

}
