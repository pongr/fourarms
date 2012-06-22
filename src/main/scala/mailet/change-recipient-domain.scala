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
 * A mailet that changes a matching recipient domain on the Mail object, so that matchers and mailets 
 * further down the processing chain will see the new recipient address instead of old one.
 */
class ChangeRecipientDomain extends PongrMailet {
  lazy val oldDomain = getInitParameter("oldDomain").trim.toLowerCase
  lazy val newDomain = getInitParameter("newDomain").trim.toLowerCase

  override def service(mail: Mail) {
    val rs = mail.getRecipients.toList.asInstanceOf[List[MailAddress]]
    val rs2 = rs map { r =>
      if (oldDomain == r.getDomain) {
        val r2 = new MailAddress(r.getLocalPart, newDomain)
        log("Changed " + r +  " to " + r2)
        r2
      } else
        r
    }
    mail.setRecipients(rs2)

    /*
    for (recipientType <- List(RecipientType.TO, RecipientType.CC, RecipientType.BCC)) {
      val newAddrs: Array[javax.mail.Address] = mail.getMessage.getRecipients(recipientType).map { addr =>
        new InternetAddress(addr.toString.replace(oldDomain, newDomain))
      }
      mail.getMessage.setRecipients(recipientType, newAddrs)
    } 
    */

  }

}
