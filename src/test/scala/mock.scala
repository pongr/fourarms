package com.pongr.fourarms.matcher

import org.specs2.mock.Mockito

import org.apache.mailet._
import org.apache.mailet.base._

trait Mocks extends Mockito {

  val addr = mock[MailAddress]
  addr.toString returns "spammer@test.com"
  addr.getLocalPart returns "spammer@test.com"

  val mail = mock[Mail]
  mail.getSender returns addr

  val matcherConfig = mock[MatcherConfig]
  matcherConfig.getCondition returns "com.pongr.fourarms.matcher.RejectLookup"

  val recipientIsInLookup = mock[RecipientIsInLookup]
  recipientIsInLookup.getMatcherConfig returns matcherConfig
}

