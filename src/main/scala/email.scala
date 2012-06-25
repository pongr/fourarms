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
import javax.mail.Header
import javax.mail.internet._
import javax.mail.{ Part, Multipart }
import scala.collection.JavaConversions._
import org.apache.commons.io.IOUtils

import com.pongr.fourarms.util.FromMethods


object EmailAddress extends FromMethods {

  def apply(s: String): EmailAddress = EmailAddress(new MailAddress(s))

  def apply(addr: MailAddress): EmailAddress = {
    val names = getFromName(addr.getUser)
    EmailAddress(addr.getLocalPart, addr.getDomain, names.map(_._1), names.map(_._2))
  }

}

case class EmailAddress(
  localPart: String,
  domain: String,
  firstName: Option[String],
  lastName: Option[String]
) {
  val address: String = "%s@%s" format (localPart, domain)
}

case class EmailPart(
  contentType: String,        // image/jpeg, text/html, text/plain...
  data: Array[Byte],          // image data, String,    String...
  fileName: Option[String],   // TODO just use headers instead of fileName, description & disposition fields?
  description: Option[String],
  disposition: Option[String],
  headers: Map[String, Seq[String]]
)

/**
 * 
 */
case class Email(
  from: EmailAddress,
  to: Seq[EmailAddress],
  subject: String,
  parts: Seq[EmailPart],
  headers: Map[String, Seq[String]],
  remoteHost: String,
  remoteAddr: String
)

object Email {

  def apply(m: Mail): Email = {
    val recipients = m.getRecipients map { addr => EmailAddress(addr.asInstanceOf[MailAddress]) }
    Email(EmailAddress(m.getSender),
                       recipients.toList,
                       m.getMessage.getSubject,
                       getEmailParts(m.getMessage),
                       getHeaders(m.getMessage.getAllHeaders, Map()),
                       m.getRemoteHost,
                       m.getRemoteAddr)
  }

  def getEmailParts(message: MimeMessage) = {
    val content = message.getContent
    if (content.isInstanceOf[Part])
      getAttachmentPart(content.asInstanceOf[Part])
    else
      Nil
  }

  def getAttachmentPart(part: Part): Seq[EmailPart] = try {
    if (part.isMimeType("multipart/*")) {
      val multipart = part.getContent().asInstanceOf[Multipart]
      var images: Seq[EmailPart] = Nil
      val attachments = for (i <- 0 until multipart.getCount) {
        images = images ++ getAttachmentPart(multipart.getBodyPart(i))
      }
      images
    } else {
      val headers = part.getAllHeaders
      List(EmailPart(part.getContentType,
                     IOUtils.toByteArray(part.getInputStream),
                     Option(part.getFileName),
                     Option(part.getDescription),
                     Option(part.getDisposition),
                     getHeaders(part.getAllHeaders, Map())))
    }
  } catch {
    case e: javax.mail.internet.ParseException => Nil
  }

  @scala.annotation.tailrec
  def getHeaders(enum: java.util.Enumeration[_], headers: Map[String, Seq[String]]): Map[String, Seq[String]] = {
    if (!enum.hasMoreElements) headers
    else {
      val tmp = enum.nextElement.asInstanceOf[Header]
      val newHeader = headers.filter(_._1 == tmp.getName).headOption match {
        case Some((dummy, existing)) => Map(tmp.getName -> (tmp.getValue +: existing))
        case _ => Map(tmp.getName -> List(tmp.getValue))
      }

      getHeaders(enum, headers ++ newHeader)
    }
  }

}
