package com.pongr.fourarms.mailet

import org.apache.mailet._
import org.apache.mailet.base._
import com.rabbitmq.client._

import com.pongr.fourarms.serializer.Serializer

class AmqpMailet extends GenericMailet {

  override def service(mail: Mail) {

    val serializerName = getInitParameter("serializer").trim
    val host = getInitParameter("host")
    val port = getInitParameter("port")
    val username = getInitParameter("username")
    val password = getInitParameter("password")
    val vhost = getInitParameter("vhost")

    val uri = "amqp://%s:%s@%s:%s/%s" format (username, password, host, port, vhost)
    val serializer = Class.forName(serializerName).newInstance().asInstanceOf[Serializer]


    // serialize
    val bytes = serializer.serialize(mail)

    val factory = new ConnectionFactory()
    factory.setUri(uri)
    val conn = factory.newConnection()

    val channel = conn.createChannel()

    channel.exchangeDeclare("testExchange", "direct", true)
    channel.queueDeclare("testQueue", true, false, false, null)
    channel.queueBind("testQueue", "testExchange", "testKey")

    channel.basicPublish("testExchange", "testKey", null, bytes)

    channel.close()
    conn.close()

    // TODO: send bytes to AMQP

  }

}
