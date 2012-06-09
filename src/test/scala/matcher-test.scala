package com.pongr.fourarms.matcher

import org.specs2.mutable._

class MatcherSpec extends Specification with Mocks {

  "Matcher test" should {

    /*
    "match email if the sender is registered as spammer in provided lookup" in {
      // Need to use spy since we're testing on a real method
      val m = spy(new SenderIsInLookup)
      m.getMatcherConfig returns rejectMatcherCfg
      m.`match`(spamMail).size must_== 0
    }
    */

    "relay emails if the recipient is in relay lookup" in {
      val m = spy(new RecipientIsInLookup)
      m.getMatcherConfig returns relayMatcherCfg

      m.matchRecipient(relayAddr) must_== true
      m.matchRecipient(spammerAddr) must_== false
    }

  }

}

