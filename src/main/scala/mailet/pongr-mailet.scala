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
import org.apache.mailet.base._

import java.util.concurrent.TimeUnit

/** Base trait for Pongr Mailets. */
trait PongrMailet extends GenericMailet {
  override def getMailetName() = getClass.getSimpleName
  override def getMailetInfo() = "Created by Pongr"
  override def init() { log(getMailetName() + " starting up...") }
  def log(mail: Mail) { log(getMailetName() + " processing " + mail.getName + " from " + mail.getSender + " to " + mail.getRecipients) }
  override def log(msg: String) { 
    try { super.log(getMailetName + " " + msg) } catch { case e => }
  }


  def getTimeUnit(t: String) = t match {
    case "minutes"      => TimeUnit.MINUTES
    case "milliseconds" => TimeUnit.MILLISECONDS
    case _              => TimeUnit.SECONDS
  }
}
