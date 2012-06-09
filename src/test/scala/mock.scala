package com.pongr.fourarms.matcher

import org.specs2.mock.Mockito

import org.apache.mailet._
import org.apache.mailet.base._

trait Mocks extends Mockito {

  val spamEmailAddress = "spammer@test.com"
  val relayEmail = "important@test.com"

  val spammerAddr = mock[MailAddress]
  spammerAddr.toString returns spamEmailAddress
  spammerAddr.getLocalPart returns spamEmailAddress

  val relayAddr = mock[MailAddress]
  relayAddr.toString returns relayEmail
  relayAddr.getLocalPart returns relayEmail

  val spamMail = mock[Mail]
  spamMail.getSender returns spammerAddr

  val rejectMatcherCfg = mock[MatcherConfig]
  rejectMatcherCfg.getCondition returns "com.pongr.fourarms.matcher.InmemoryRejectLookup"

  val relayMatcherCfg = mock[MatcherConfig]
  relayMatcherCfg.getCondition returns "com.pongr.fourarms.matcher.InmemoryRelayLookup"
}
