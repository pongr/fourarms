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

  lazy val lookupClassName = getMatcherConfig.getCondition

  // Creating an instance of the email lookup
  lazy val lookupInstance: Lookup = Class.forName(lookupClassName).newInstance.asInstanceOf[Lookup]

  def exist_?(e: String) = lookupInstance.exist_?(e)

}

class RecipientIsInLookup extends GenericRecipientMatcher with ElementsFromLookup {

  override def matchRecipient(recipient: MailAddress): Boolean = {
    val r = recipient.getLocalPart.trim.toLowerCase
    val matchResult = exist_?(r)

    if (matchResult)
      // use try/catch to avoid NPE when testing with mock object
      try { log("Recipient %s matched!".format(r)) } catch { case e => }

    matchResult
  }

}

/*
  Matches if sender is in the file specified in the condition. Usage would be like:
  <mailet match="SenderIsInFile=com.pongr.fouramrs.matcher.SimpleDbLookup" class="ToProcessor">
*/
class SenderIsInLookup extends GenericMatcher with FromMethods with ElementsFromLookup {

  override def `match`(mail: Mail): JCollection[_] = {
    val sender = getFromEmail(mail)
    if (exist_?(sender)) {
      log("Sender %s matched!".format(sender))
      mail.getRecipients
    }
    else
      Nil
  }

}

class SenderDomainIsInLookup extends GenericMatcher with FromMethods with ElementsFromLookup {

  override def `match`(mail: Mail): JCollection[_] = {
    getDomain(mail) match {
      case Some(domain) if (exist_?(domain)) =>
        log("SenderDomain %s matched!".format(domain))
        mail.getRecipients 
      case _ => Nil
    }
  }

}
