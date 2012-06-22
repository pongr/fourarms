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
import javax.mail.{ Part, Multipart }
import scala.collection.JavaConversions._
import org.apache.commons.io.IOUtils


object EmailAddress {

  def apply(s: String): EmailAddress = EmailAddress(new MailAddress(s))

  def apply(addr: MailAddress): EmailAddress = {
    EmailAddress(addr.getLocalPart, addr.getDomain, None, None)
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

object EmailPart {

  val mime2Extension = Map("image/jpeg" -> "jpg", 
                           "image/jpg" -> "jpg", 
                           "image/pjpeg" -> "jpg", 
                           "image/bmp" -> "bmp", 
                           "image/gif" -> "gif", 
                           "image/png" -> "png", 
                           "image/x-png" -> "png")
  val mimeTypes = mime2Extension.keys
  
  val extension2Mime = Map("jpg" -> "image/jpeg", 
                           "jpeg" -> "image/jpeg", 
                           "bmp" -> "image/bmp",
                           "png" -> "image/png", 
                           "gif" -> "image/gif")

  val extensions = extension2Mime.keys

  def fileName(p: Part): Option[String] = if (p.getFileName == null) None else Some(p.getFileName.toLowerCase.trim)
  
  def rawExtension(p: Part): Option[String] = fileName(p) flatMap { s => 
    val i = s lastIndexOf '.'
    if (i >= 0) Some(s substring (i+1)) else None
  }
  
  def extension(p: Part): Option[String] = rawExtension(p) flatMap { e => extensions find { _ == e } }

  def mime(p: Part): Option[String] = mimeTypes find { p isMimeType _ }


  def apply(message: MimeMessage) = {
    val content = message.getContent
    if (content.isInstanceOf[Part])
      getImageFilesAttachmentPart(content.asInstanceOf[Part])
    else
      Nil
  }

  def getImageFilesAttachmentPart(part: Part): Seq[EmailPart] = try { 
    if (part.isMimeType("multipart/*")) { 
      val multipart = part.getContent().asInstanceOf[Multipart]
      var images: Seq[EmailPart] = Nil
      val attachments = for (i <- 0 until multipart.getCount) { 
        images = images ++ getImageFilesAttachmentPart(multipart.getBodyPart(i))
      } 
      images
    } else part match { 
      case ImageType(mime, extension) => List(EmailPart(part.getContentType,
                                                        IOUtils.toByteArray(part.getInputStream),
                                                        Option(part.getFileName),
                                                        Option(part.getDescription),
                                                        Option(part.getDisposition),
                                                        Map()))
                                                        /*part.getAllHeaders))*/
      case _ => Nil
    }
  } catch {
    case e: javax.mail.internet.ParseException => Nil
  }

  object ImageType {
    def unapply(p: Part): Option[(String, String)] = 
      mime(p).map(m => (m, mime2Extension(m))).orElse(extension(p).map(e => (extension2Mime(e), e)))
  }

}

case class EmailPart(
  contentType: String,        // image/jpeg, text/html, text/plain...
  data: Array[Byte],          // image data, String,    String...
  fileName: Option[String],   // TODO just use headers instead of fileName, descripption & disposition fields?
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
                       Nil, //TODO
                       Map(), // TODO
                       m.getRemoteHost,
                       m.getRemoteAddr)
  }

}
