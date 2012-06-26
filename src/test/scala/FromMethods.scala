package com.pongr.fourarms.util

import org.specs2.mutable._

import javax.mail.internet._

import com.pongr.fourarms.mail._
import com.pongr.fourarms.Helper

class FromMethodsSpec extends Specification with FromMethods with Helper {

  "FromMethodsSpec test" should {

    "get sender email as string from mail object" in {
      getFromEmail(mail1) must_== "byamba@pongr.com"
      getFromEmail(mail2) must_== "zcox@pongr.com"
    }

    "get sender email address as InternetAddress" in {
      getFromAddress(mail1) must_== Some(new InternetAddress ("Byamba Tumurkhuu <byamba@pongr.com>"))
      getFromAddress(mail2) must_== Some(new InternetAddress("Zach Cox <zcox@pongr.com>"))
    }

    "get sender domain as string from mail object" in {
      getFromDomain(mail1) must_== Some("pongr.com")
      getFromDomain(mail2) must_== Some("pongr.com")
    }

    "get sender first name and lastname" in {
      getFromName(mail1) must_== (Some("Byamba"), Some("Tumurkhuu"))
      getFromName(mail2) must_== (Some("Zach"), Some("Cox"))

      getFromName(new InternetAddress("First Last <email@host.com>")) must_== (Some("First"), Some("Last"))
      getFromName(new InternetAddress("FIRST LAST <email@host.com>")) must_== (Some("First"), Some("Last"))
      getFromName(new InternetAddress("first last <email@host.com>")) must_== (Some("First"), Some("Last"))
      getFromName(new InternetAddress("\"First Last\" <email@host.com>")) must_== (Some("First"), Some("Last"))
      getFromName(new InternetAddress("\"Last, First\" <email@host.com>")) must_== (Some("First"), Some("Last"))
      getFromName(new InternetAddress("First Middle Last <email@host.com>")) must_== (Some("First"), Some("Middle last"))
      getFromName(new InternetAddress("First <email@host.com>")) must_== (Some("First"), None)
      getFromName(new InternetAddress("email@host.com", "Last, First")) must_== (Some("First"), Some("Last"))
    }

  }

}
