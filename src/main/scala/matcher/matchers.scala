package com.pongr.fourarms.matcher

import org.apache.mailet._
import org.apache.mailet.base._
import scala.collection.JavaConversions._
import java.util.{Collection => JCollection}

import com.pongr.fourarms.util.FromMethods

trait Lookup {
  def exist_?(element: String): Boolean
}

trait ElementsFromLookup { this: Matcher => 

  def condition = getMatcherConfig.getCondition
  def lookupClassName = condition.split(",").head

  // Creating an instance of the email lookup
  def lookupInstance: Lookup = Class.forName(lookupClassName)
                                    .getDeclaredConstructor(classOf[String])
                                    .newInstance(condition).asInstanceOf[Lookup]

  def exist_?(e: String) = lookupInstance.exist_?(e)

}

class RecipientIsInLookup extends GenericRecipientMatcher with ElementsFromLookup {

  override def matchRecipient(recipient: MailAddress): Boolean =
    exist_?(recipient.getLocalPart.trim.toLowerCase)

}

/*
  Matches if sender is in the file specified in the condition. Usage would be like:
  <mailet match="SenderIsInFile=com.pongr.fouramrs.matcher.SimpleDbLookup, com.domain.AwsCred" class="ToProcessor">
*/
class SenderIsInLookup extends GenericMatcher with FromMethods with ElementsFromLookup {

  override def `match`(mail: Mail): JCollection[_] = {
    val sender = getFromEmail(mail)
    if (exist_?(sender))
      mail.getRecipients
    else
      Nil
  }

}

class SenderDomainIsInLookup extends GenericMatcher with FromMethods with ElementsFromLookup {

  override def `match`(mail: Mail): JCollection[_] = {
    getDomain(mail) match {
      case Some(domain) => if (exist_?(domain)) mail.getRecipients else Nil
      case _ => Nil
    }
  }

}
