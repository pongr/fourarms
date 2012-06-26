package com.pongr.fourarms.mailet

import org.specs2.mutable._
import scala.collection.JavaConversions._
import org.apache.mailet.MailAddress
import javax.mail.internet.InternetAddress

import com.pongr.fourarms._
import com.pongr.fourarms.util.FromMethods

class MailetSpec extends Specification with Helper with FromMethods {

  "MailetSpec test" should {

    "Change recipient" in {
      val mail = mail1
      val mailet = new ChangeRecipient {
        override lazy val oldRecipient = "dew@fourarms.pongrdev.com"
        override lazy val newRecipient = "dew-pic@pongr.com"
      }

      mailet.service(mail)

      mail.getRecipients.contains(new MailAddress("dew-pic@pongr.com")) must_== true
      mail.getRecipients.contains(new MailAddress("dew@fourarms.pongrdev.com")) must_== false

      mail.getMessage.getAllRecipients.contains(new InternetAddress("dew-pic@pongr.com")) must_== true
      mail.getMessage.getAllRecipients.contains(new InternetAddress("dew@fourarms.pongrdev.com")) must_== false
    }

    "Change recipient domain" in {
      val mail = mail1
      val mailet = new ChangeRecipientDomain {
        override lazy val oldDomain = "fourarms.pongrdev.com"
        override lazy val newDomain = "pongr.com"
      }

      mailet.service(mail)

      mail.getRecipients.contains(new MailAddress("dew@pongr.com")) must_== true
      mail.getRecipients.contains(new MailAddress("dew@fourarms.pongrdev.com")) must_== false
      mail.getMessage.getAllRecipients.contains(new InternetAddress("dew@pongr.com")) must_== true
      mail.getMessage.getAllRecipients.contains(new InternetAddress("dew@fourarms.pongrdev.com")) must_== false

      mail.getRecipients.contains(new MailAddress("pepsi@pongr.com")) must_== true
      mail.getRecipients.contains(new MailAddress("pepsi@fourarms.pongrdev.com")) must_== false
      mail.getMessage.getAllRecipients.contains(new InternetAddress("pepsi@pongr.com")) must_== true
      mail.getMessage.getAllRecipients.contains(new InternetAddress("pepsi@fourarms.pongrdev.com")) must_== false
    }

  }

}
