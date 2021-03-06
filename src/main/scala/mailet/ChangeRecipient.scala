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
import scala.collection.JavaConversions._
import javax.mail.Message.RecipientType
import javax.mail.internet.InternetAddress

/**
 * A mailet that changes a matching recipient on the Mail object, so that matchers and mailets 
 * further down the processing chain will see the new recipient address instead of old one.
 */
class ChangeRecipient extends PongrMailet {
  lazy val oldRecipient = getInitParameter("oldRecipient").trim.toLowerCase
  lazy val newRecipient = getInitParameter("newRecipient").trim.toLowerCase

  override def service(mail: Mail) {
    val rs = mail.getRecipients.toList.asInstanceOf[List[MailAddress]]
    val rs2 = rs map { r =>
      if (oldRecipient == r.toString.trim.toLowerCase) {
        log("Changed " + r + " to " + newRecipient)
        new MailAddress(newRecipient)
      } else
        r
    }
    mail.setRecipients(rs2)

    for (recipientType <- List(RecipientType.TO, RecipientType.CC, RecipientType.BCC)) {
      val recipients= mail.getMessage.getRecipients(recipientType)

      if (recipients != null) {
        val newAddrs = recipients.map { tmp =>
          val addr = tmp.asInstanceOf[InternetAddress]
          if (addr.getAddress == oldRecipient)
            new InternetAddress(newRecipient, addr.getPersonal)
          else 
            tmp
        }

        mail.getMessage.setRecipients(recipientType, newAddrs)
      }

    }

  }
}

