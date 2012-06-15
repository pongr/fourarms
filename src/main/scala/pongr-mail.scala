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

package com.pongr.fourarms.mail

import org.apache.mailet._
import javax.mail.internet._

/**
 * MimeMessage wrapper
 */
object PongrMail {

  def apply(m: Mail): PongrMail = 
    PongrMail(m.getSender, m.getRecipients, m.getRemoteHost, m.getRemoteAddr, m.getMessage)

}

case class PongrMail(sender: MailAddress, 
                     recipients: java.util.Collection[_],
                     remoteHost: String,
                     remoteAddr: String,
                     message: MimeMessage
                    )
