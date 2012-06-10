package com.pongr.fourarms.matcher

import org.specs2.mutable._

class MatcherSpec extends Specification with Mocks {

  "Matcher test" should {

    "match email if the sender is registered as spammer in provided lookup" in {
      // Need to use spy since we're testing on a real method
      val m = spy(new SenderIsInLookup)
      m.getMatcherConfig returns rejectMatcherCfg
      m.exist_?(spamEmailAddress) must_== true
      m.exist_?(relayEmail) must_== false
    }

    "relay emails if the recipient is in the relay lookup" in {
      val m = spy(new RecipientIsInLookup)
      m.getMatcherConfig returns relayMatcherCfg

      // match the email
      m.matchRecipient(relayAddr) must_== true

      // don't match if the email is not in the lookup
      m.matchRecipient(spammerAddr) must_== false
    }

    "match domains if the sender domain is in the lookup" in {
      val m = spy(new DomainIsInLookup)
      m.getMatcherConfig returns rejectDomainMatcherCfg

      // match the email
      m.exist_?(spamDomain) must_== true

      // don't match if the email is not in the lookup
      m.exist_?(goodDomain) must_== false
    }

  }

}
