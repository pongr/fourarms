package com.pongr.fourarms.mailet

import org.apache.mailet._
import org.apache.mailet.base._

import com.pongr.fourarms.serializer.Serializer

class AmqpMailet extends GenericMailet {

  override def service(mail: Mail) {

    val serializerName = getInitParameter("serializer").trim

    val serializer = Class.forName(serializerName).newInstance().asInstanceOf[Serializer]

    val bytes = serializer.serialize(mail)

    // TODO: send bytes to AMQP

  }

}
