package com.pongr.fourarms.mail

import org.specs2.mutable._

import java.io._
import org.apache.commons.io.IOUtils
import javax.mail.internet._

import com.pongr.fourarms.mail._
import com.pongr.fourarms.Helper

class EmailSpec extends Specification with Helper {

  "EmailSpec test" should {

    "Create Email from plain MimeMessage" in {

      val mail = Email(createMail("/MimeMessage-plain"))

      mail.from must_== EmailAddress("zcox", "pongr.com", Some("Zach"),Some("Cox"))

      mail.to must_== List(EmailAddress("plaintext", "fourarms.pongrdev.com", None, None))

      mail.subject must_== "Plaintext"

      mail.parts.size must_== 1

      mail.headers.size must_== 10
      mail.headers.get("MIME-Version").get must_== List("1.0")
      mail.headers.get("Content-Type").get must_== List("text/plain; charset=ISO-8859-1")
      mail.headers.get("Received").get.size must_== 5
      mail.headers.get("Date").get must_== List("Fri, 22 Jun 2012 16:19:40 -0500")
      mail.headers.get("From").get must_== List("Zach Cox <zcox@pongr.com>")
      
      mail.parts(0).contentType             must_== "text/plain; charset=ISO-8859-1"
      mail.parts(0).headers.size            must_== 10
      mail.parts(0).fileName                must_== None
      mail.parts(0).description             must_== None
      mail.parts(0).disposition             must_== None
      (new String(mail.parts(0).data)).trim must_== "This is a simple plaintext email."

    }

    "Create Email from multipart MimeMessage(plain/html)" in {

      val mail = Email(createMail("/MimeMessage-plain-html"))

      mail.from must_== EmailAddress("zcox", "pongr.com", Some("Zach"),Some("Cox"))

      mail.to must_== List(EmailAddress("html", "fourarms.pongrdev.com", None, None))

      mail.subject must_== "HTML"

      mail.parts.size must_== 2

      mail.headers.size must_== 10
      mail.headers.get("MIME-Version").get must_== List("1.0")
      mail.headers.get("Content-Type").get must_== List("multipart/alternative; boundary=14dae93405759258f204c31636dd")
      mail.headers.get("Received").get.size must_== 5
      mail.headers.get("Date").get must_== List("Fri, 22 Jun 2012 16:21:19 -0500")
      mail.headers.get("From").get must_== List("Zach Cox <zcox@pongr.com>")
      
      mail.parts(0).contentType             must_== "text/plain; charset=ISO-8859-1"
      mail.parts(0).headers.size            must_== 1
      mail.parts(0).fileName                must_== None
      mail.parts(0).description             must_== None
      mail.parts(0).disposition             must_== None
      (new String(mail.parts(0).data)).trim must_== "This is a *rich* *email.*"

      mail.parts(1).contentType             must_== "text/html; charset=ISO-8859-1"
      mail.parts(1).headers.size            must_== 1
      mail.parts(1).fileName                must_== None
      mail.parts(1).description             must_== None
      mail.parts(1).disposition             must_== None
      (new String(mail.parts(1).data)).trim must_== """<font color="#ff0000">This</font> is a <b>rich</b> <i>email.</i>"""

    }

    "Create Email with image attached" in {

      val mail = Email(createMail("/MimeMessage-plain-jpeg"))

      mail.from must_== EmailAddress("zcox", "pongr.com", Some("Zach"),Some("Cox"))

      mail.to must_== List(EmailAddress("jpeg", "fourarms.pongrdev.com", None, None))

      mail.subject must_== "jpeg"

      mail.parts.size must_== 2

      mail.headers.size must_== 10
      mail.headers.get("MIME-Version").get must_== List("1.0")
      mail.headers.get("Content-Type").get must_== List("multipart/mixed; boundary=14dae934057561fd2c04c3163b93")
      mail.headers.get("Received").get.size must_== 5
      mail.headers.get("Date").get must_== List("Fri, 22 Jun 2012 16:22:39 -0500")
      mail.headers.get("From").get must_== List("Zach Cox <zcox@pongr.com>")
      
      mail.parts(0).contentType             must_== "text/plain; charset=ISO-8859-1"
      mail.parts(0).headers.size            must_== 2
      mail.parts(0).headers.get("Content-Transfer-Encoding").get must_== List("quoted-printable")
      mail.parts(0).fileName                must_== None
      mail.parts(0).description             must_== None
      mail.parts(0).disposition             must_== None
      (new String(mail.parts(0).data)).trim must_== "This�email is plaintext with a jpeg attached."

      
      mail.parts(1).contentType.contains("image/jpeg;") must_== true
      mail.parts(1).contentType.contains("name=\"07d02602-5aea-49a1-a12d-c5a59936290fw425.jpg\"") must_== true
      mail.parts(1).fileName     must_== Some("07d02602-5aea-49a1-a12d-c5a59936290fw425.jpg")
      mail.parts(1).description  must_== None
      mail.parts(1).disposition  must_== Some("attachment")

      mail.parts(1).headers.size must_== 4
      mail.parts(1).headers.get("Content-Transfer-Encoding").get must_== List("base64")
      mail.parts(1).headers.get("X-Attachment-Id").get must_== List("f_h3rrmx7q0")
     //  IOUtils.contentEquals(new ByteArrayInputStream(mail.parts(1).data), getClass.getResourceAsStream("/07d02602-5aea-49a1-a12d-c5a59936290fw425.jpg")) must_== true

    }


    "Create Email with image attached and multiple recipients" in {

      val mail = Email(createMail("/MimeMessage-plain-jpeg-multipleRecipient"))

      mail.from must_== EmailAddress("byamba", "pongr.com", Some("Byamba"),Some("Tumurkhuu"))

      mail.to must_== List(EmailAddress("pepsi", "fourarms.pongrdev.com", None, None), 
                           EmailAddress("dew", "fourarms.pongrdev.com", None, None))

      mail.subject must_== "Yo subject!"

      mail.parts.size must_== 3

      mail.headers.size must_== 10
      mail.headers.get("MIME-Version").get must_== List("1.0")
      mail.headers.get("Content-Type").get must_== List("multipart/mixed; boundary=0016e6dd8a23ccee0f04c350490b")
      mail.headers.get("Received").get.size must_== 5
      mail.headers.get("Date").get must_== List("Mon, 25 Jun 2012 13:38:09 -0500")
      mail.headers.get("From").get must_== List("Byamba Tumurkhuu <byamba@pongr.com>")
      
      mail.parts(0).contentType             must_== "text/plain; charset=ISO-8859-1"
      mail.parts(0).headers.size            must_== 1
      mail.parts(0).fileName                must_== None
      mail.parts(0).description             must_== None
      mail.parts(0).disposition             must_== None
      (new String(mail.parts(0).data)).trim must_== "Dabooooo"

      mail.parts(1).contentType             must_== "text/html; charset=ISO-8859-1"
      mail.parts(1).headers.size            must_== 1
      mail.parts(1).fileName                must_== None
      mail.parts(1).description             must_== None
      mail.parts(1).disposition             must_== None
      (new String(mail.parts(1).data)).trim must_== "Dabooooo"

      mail.parts(2).contentType  must_== "image/jpeg; name=\"pepsi.jpg\""
      mail.parts(2).fileName     must_== Some("pepsi.jpg")
      mail.parts(2).description  must_== None
      mail.parts(2).disposition  must_== Some("attachment")

      mail.parts(2).headers.size must_== 4
      mail.parts(2).headers.get("Content-Disposition").get must_== List("attachment; filename=\"pepsi.jpg\"")
      mail.parts(2).headers.get("Content-Transfer-Encoding").get must_== List("base64")
      mail.parts(2).headers.get("X-Attachment-Id").get must_== List("f_h3vw353e1")
      IOUtils.contentEquals(new ByteArrayInputStream(mail.parts(2).data), getClass.getResourceAsStream("/pepsi.jpg")) must_== true

    }


  }

}
