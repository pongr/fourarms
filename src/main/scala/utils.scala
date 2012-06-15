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

package com.pongr.fourarms.util

import org.apache.mailet.Mail
import javax.mail.Address
import javax.mail.internet.InternetAddress
import org.apache.commons.lang.StringUtils.isBlank

/**
 * Provides useful methods to extract different kind of information from Mail object.
 */
trait FromMethods {

  def defaultLastName = "X"

  def getFrom(mail: Mail): (String, Option[(String, String)]) = {
    val from = getFromEmail(mail)
    val fromName = getFromName(mail)

    // For some Sprint users we get From: 123-456-7890 <donotreply@pm.sprint.com>
    // Sprint SMS email addresses are like 1234567890@messaging.sprintpcs.com
    if ("donotreply@pm.sprint.com" == from) {
      val tmpFrom = fromName map { case (first, last) => 
                      first.replaceAll("\\D", "") + "@messaging.sprintpcs.com" 
                    } getOrElse ""
      (tmpFrom, None)
    }
    else 
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

  def getDomain(mail: Mail): Option[String] = {
    val sender = getFromEmail(mail)
    val index = sender lastIndexOf "@"
    if (index >= 0 && index < sender.length - 1)
      Some(sender substring (index + 1))
    else
      None
  }

  //From: "First, Last" <First.Last@host.com>
  //From: "First Last" <email@host.com>
  //From: First Last <email@host.com>
  //Last, First
  //First Last
  //First
  def getFromName(mail: Mail): Option[(String, String)] = {
    val LastFirst = """(.+?),\s*(.+)""".r
    val FirstLast = """(.+?)\s+(.+)""".r

    val names = getFromAddress(mail) match {
      case Some(address) if !isBlank(address.getPersonal) => 
        address.getPersonal.replaceAll("\"", "").trim.toLowerCase match {
          case LastFirst(last, first) => Some((first, last))
          case FirstLast(first, last) => Some((first, last))
          case s: String if s contains "@" => None // skip "username@pm.sprint.com" <username@pm.sprint.com>
          case name: String => Some((name, defaultLastName))
        }
      case _ => None
    }

    // capitalize names
    names.map(x => (x._1.capitalize, x._2.capitalize))
  }

}

