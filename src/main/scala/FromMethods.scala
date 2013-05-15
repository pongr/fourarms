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
 * Provides few useful methods to extract different kind of information from Mail object.
 */
trait FromMethods {

  def getFromEmail(mail: Mail): String = getFromAddress(mail) match {
    case Some(a) => a.getAddress.toLowerCase
    case _ => Option(mail.getSender).map(_.toString.toLowerCase).getOrElse("")
  }

  def getFromAddress(mail: Mail): Option[InternetAddress] =
    Option(mail.getMessage.getFrom).flatMap(_.headOption.map(_.asInstanceOf[InternetAddress]))
    

  def getFromDomain(mail: Mail): Option[String] = {
    val sender = getFromEmail(mail)
    val index = sender lastIndexOf "@"
    if (index >= 0 && index < sender.length - 1)
      Some(sender substring (index + 1))
    else
      None
  }

  /**
   * Returns (firstName, lastName)
   */
  def getFromName(mail: Mail): (Option[String], Option[String]) = getFromAddress(mail) match {
    case Some(address) => getFromName(address)
    case _ => (None, None)
  }

  def getFromName(addr: InternetAddress): (Option[String], Option[String]) = 
    if (isBlank(addr.getPersonal))
      (None, None)
    else {
      val LastFirst = """(.+?),\s*(.+)""".r
      val FirstLast = """(.+?)\s+(.+)""".r
      val names=  addr.getPersonal.replaceAll("\"", "").trim.toLowerCase match {
        case LastFirst(last, first) => (Some(first), Some(last))
        case FirstLast(first, last) => (Some(first), Some(last))
        case name: String => (Some(name), None)
      }

      // capitalize names
      (names._1.map(_.capitalize), names._2.map(_.capitalize))
    }

}

