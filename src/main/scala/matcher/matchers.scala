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

package com.pongr.fourarms.matcher

import org.apache.mailet._
import org.apache.mailet.base._
import scala.collection.JavaConversions._
import java.util.{Collection => JCollection}

import com.pongr.fourarms.util.FromMethods

trait Lookup {
  /**
   * Returns true if the specified string exists in the lookup
   */
  def exist_?(element: String): Boolean
}

/**
 * A reusable trait that contains LookUp trait instance creation and the exits_? method.
 */
trait ElementsFromLookup { this: Matcher => 

  private lazy val lookupClassName = getMatcherConfig.getCondition

  // Creating an instance of the lookup
  private lazy val lookupInstance: Lookup = Class.forName(lookupClassName).newInstance.asInstanceOf[Lookup]

  def exist_?(e: String) = lookupInstance.exist_?(e)

}

/**
 * Matches if the recipient of a received email exists in the lookup specified in the condition.
 * James xml config:
 *
 * {{{
 *   <mailet match="RecipientIsInLookup=com.pongr.fouramrs.matcher.RecipientLookup" class="ToProcessor">
 *     <processor>relay</processor>
 *   </mailet>
 * }}}
 */
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

/**
 * Matches if the sender of a received email exists in the lookup specified in the condition.
 * James xml config:
 *
 * {{{
 *   <mailet match="SenderIsInLookup=com.pongr.fouramrs.matcher.SenderSpamLookup" class="ToProcessor">
 *     <processor>reject</processor>
 *   </mailet>
 * }}}
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

/**
 * Matches if sender domain is in the lookup specified in the condition. 
 * James xml config:
 *
 * {{{
 *   <mailet match="SenderDomainIsInLookup=com.pongr.fouramrs.matcher.SenderDomainSpamLookup" class="ToProcessor">
 *     <processor>reject</processor>
 *   </mailet>
 * }}}
 */
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
