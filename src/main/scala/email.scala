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
import javax.mail.{ Multipart, Header, Address }
import javax.mail.internet._
import javax.mail.{ Part, Multipart }
import scala.collection.JavaConversions._
import org.apache.commons.io.IOUtils

import com.pongr.fourarms.util.FromMethods

object EmailAddress extends FromMethods {

  def apply(addr: Address): EmailAddress = if (addr != null) {
    val names = getFromName(addr.toString)
    val a = new MailAddress(new InternetAddress(addr.toString))
    EmailAddress(a.getLocalPart, a.getDomain, names.map(_._1), names.map(_._2))
  } else EmailAddress("", "", None, None)

}


/**
 * Represents an email address.
 * @param localPart username from "username@host.com".
 * @param domain domain without "@". For example: pongr.com
 * @param firstName First name
 * @param lastName Last name
 */
case class EmailAddress(
  localPart: String,
  domain: String,
  firstName: Option[String],
  lastName: Option[String]
) {
  val address: String = "%s@%s" format (localPart, domain)
}

/**
 * Representation of the message's part.
 * @param contentType "Content-Type" header field.
 * @param data Array[Byte] representation of this part's content.
 * @param fileName "filename" parameter from the "Content-Disposition" header field of this part.
 * @param description "Content-Description" header field.
 * @param disposition "Content-Disposition" header field. 
 * @param headers All headers associated with this part.
 */
case class EmailPart(
  contentType: String,        
  data: Array[Byte],          
  fileName: Option[String],
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
) {

  /**
   * This private field helps the compiler to load EmailPart class 
   * when it deserializes Seq[EmailPart] using Java's native serialization.
   */
  private val email = EmailPart("", Array(), None, None, None, Map())

}

object Email extends FromMethods {

  def apply(m: Mail): Email = {
    val names = getFromName(m)
    val addr = new MailAddress(getFromEmail(m))

    Email(EmailAddress(addr.getLocalPart, addr.getDomain, names.map(_._1), names.map(_._2)),
          m.getMessage.getAllRecipients.map(EmailAddress(_)).toList,
          m.getMessage.getSubject,
          getEmailParts(m.getMessage),
          getHeaders(m.getMessage.getAllHeaders, Map()),
          m.getRemoteHost,
          m.getRemoteAddr)
  }

  def getEmailParts(part: Part): Seq[EmailPart] = try {
    if (part.isMimeType("multipart/*")) {
      val multipart = part.getContent().asInstanceOf[Multipart]
      var images: Seq[EmailPart] = List()
      for (i <- 0 until multipart.getCount) {
        images = images ++ getEmailParts(multipart.getBodyPart(i))
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
