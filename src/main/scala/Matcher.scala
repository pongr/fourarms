package com.pongr.fourarms.matcher

import org.apache.mailet._
import org.apache.mailet.base._
import scala.io._
import scala.collection.JavaConversions._
import java.util.{Collection => JCollection}

import com.pongr.fourarms.util.FromMethods

class RecipientIsInRepo extends GenericRecipientMatcher {

  def repo = getMatcherConfig.getCondition

  override def matchRecipient(recipient: MailAddress): Boolean = {
    val email = recipient.getLocalPart.trim.toLowerCase
    println(email)
    true
  }
}

/** Matches if sender is in the file specified in the condition. Usage would be like:
 * <mailet match="SenderIsInFile=something" class="ToProcessor">
 */
class SenderIsInRepo extends GenericMatcher with FromMethods {
  override def `match`(mail: Mail): JCollection[_] = {
    //val sender = toString(mail.getSender)
    val sender = getFromEmail(mail)
    val es = Nil
    log("Testing if sender %s is contained in reject list %s".format(sender, es))
    if (es contains sender) {
      log("Rejecting because sender is %s".format(sender))
      mail.getRecipients
    } else 
      Nil
  }
  def toString(a: MailAddress) = if (a == null) "" else (a.getLocalPart.trim.toLowerCase + "@" + a.getDomain.trim.toLowerCase)
}
