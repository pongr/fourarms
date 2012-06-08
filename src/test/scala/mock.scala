package com.pongr.fourarm

import org.mockito.Mockito
import org.mockito.Mockito._

import org.apache.mailet._


trait MockMail extends Mockito { 

  val mail = mock(classOf[Mail])
  val addr = mock(classOf[MailAddress])

  //mail.getSender returns addr
}

