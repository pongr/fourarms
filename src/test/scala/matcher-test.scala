package com.pongr.fourarms.matcher

import org.specs2.mutable._


class MatcherSpec extends Specification with Mocks {

  "Matcher test" should {

    "reject emails" in {

      println("TESTING >>>>>>>>>>>>>>>")
      recipientIsInLookup.matchRecipient(addr) must_== false

    }

  }

}

