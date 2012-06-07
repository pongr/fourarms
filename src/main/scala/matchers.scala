package com.pongr.fourarms.matcher

import org.apache.mailet._
import org.apache.mailet.base._
import scala.collection.JavaConversions._
import java.util.{Collection => JCollection}

import com.pongr.fourarms.util.FromMethods

trait Lookup {
  def elements: List[String]
}

trait ElementsFromLookup { this: Matcher => 

  val lookupClassName = getMatcherConfig.getCondition

  // Creating an instance of the email lookup 
  lazy val lookupInstance: Lookup = 
    Class.forName(lookupClassName).newInstance.asInstanceOf[Lookup]

  def elements = lookupInstance.elements

}

class RecipientIsInLookup extends GenericRecipientMatcher with ElementsFromLookup {

  override def matchRecipient(recipient: MailAddress): Boolean =
    elements contains recipient.getLocalPart.trim.toLowerCase

}

/*
  Matches if sender is in the file specified in the condition. Usage would be like:
  <mailet match="SenderIsInFile=org.domain.SimpleDbLookup" class="ToProcessor">
*/
class SenderIsInLookup extends GenericMatcher with FromMethods with ElementsFromLookup {

  override def `match`(mail: Mail): JCollection[_] = {
    val sender = getFromEmail(mail)
    if (elements contains sender)
      mail.getRecipients
    else
      Nil
  }

  def toString(addr: MailAddress) =
    if (addr == null)
      ""
    else
      addr.getLocalPart.trim.toLowerCase + "@" + addr.getDomain.trim.toLowerCase

}

class DomainIsInLookup extends GenericMatcher with ElementsFromLookup {

  override def `match`(mail: Mail): JCollection[_] = Nil

}
