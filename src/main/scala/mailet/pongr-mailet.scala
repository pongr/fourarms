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
  override def log(msg: String) { super.log(getMailetName + " " + msg) }


  def getTimeUnit(t: String) = t match {
    case "minutes"      => TimeUnit.MINUTES
    case "milliseconds" => TimeUnit.MILLISECONDS
    case _              => TimeUnit.SECONDS
  }
}
