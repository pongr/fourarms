package com.pongr.fourarms.util

import org.apache.mailet.Mail
import javax.mail.Address
import javax.mail.internet.InternetAddress
import org.apache.commons.lang.StringUtils.isBlank

trait FromMethods {
  def getFrom(mail: Mail): (String, Option[(String, String)]) = {
    var from = getFromEmail(mail)
    var fromName = getFromName(mail)

    //for some Sprint users we get From: 123-456-7890 <donotreply@pm.sprint.com>
    //Sprint SMS email addresses are like 1234567890@messaging.sprintpcs.com
    if ("donotreply@pm.sprint.com" == from) {
      from = fromName map { case (first, last) => first.replaceAll("\\D", "") + "@messaging.sprintpcs.com" } getOrElse ""
      fromName = None
    }
    (from, fromName)
  }
  
  def getFromEmail(mail: Mail): String = getFromAddress(mail) match {
    case Some(a) => a.getAddress.toLowerCase
    case _ => Option(mail.getSender).map(_.toString.toLowerCase).getOrElse("")
  }

  def getFromAddress(mail: Mail): Option[InternetAddress] = mail.getMessage.getFrom match {
    case a: Array[Address] if !a.isEmpty => Some(a(0).asInstanceOf[InternetAddress])
    case _ => None
  }

  //From: "First, Last" <First.Last@host.com>
  //From: "First Last" <email@host.com>
  //From: First Last <email@host.com>
  //Last, First
  //First Last
  //First
  val LastFirst = """(.+?),\s*(.+)""".r
  val FirstLast = """(.+?)\s+(.+)""".r
  def getFromName(mail: Mail): Option[(String, String)] = getFromAddress(mail) match {
    case Some(address) if !isBlank(address.getPersonal) => address.getPersonal.replaceAll("\"", "").trim.toLowerCase match {
      case LastFirst(last, first) => Some((first.capitalize, last.capitalize))
      case FirstLast(first, last) => Some((first.capitalize, last.capitalize))
      // Sprint MMS From header is like: "username@pm.sprint.com" <username@pm.sprint.com>
      case s: String if s contains "@" => None
      case name: String => Some((name.capitalize, "X"))
    }
    case _ => None
  }
}

