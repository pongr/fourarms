package com.pongr.fourarms.matcher

import org.apache.mailet._
import org.apache.mailet.base._
import scala.collection.JavaConversions._
import java.util.{Collection => JCollection}

import com.pongr.fourarms.util.FromMethods

trait Repo {
  def emails: List[String]
}

trait EmailsFromRepo { this: Matcher => 

  val repoClassName = getMatcherConfig.getCondition

  // Creating an instance of the email repository
  lazy val repoInstance: Repo = 
    Class.forName(repoClassName).newInstance.asInstanceOf[Repo]

  def emails = repoInstance.emails
}

class RecipientIsInRepo extends GenericRecipientMatcher with EmailsFromRepo {

  override def matchRecipient(recipient: MailAddress): Boolean = 
    emails contains recipient.getLocalPart.trim.toLowerCase

}

/** Matches if sender is in the file specified in the condition. Usage would be like:
 * <mailet match="SenderIsInFile=SimpleDbRepo" class="ToProcessor">
 */
class SenderIsInRepo extends GenericMatcher with FromMethods with EmailsFromRepo {

  override def `match`(mail: Mail): JCollection[_] = {
    val sender = getFromEmail(mail)
    log("Testing if sender %s is contained in the repo %s".format(sender, emails))
    if (emails contains sender) {
      mail.getRecipients
    } else
      Nil
  }

  def toString(addr: MailAddress) =
    if (addr == null)
      ""
    else
      addr.getLocalPart.trim.toLowerCase + "@" + addr.getDomain.trim.toLowerCase

}
