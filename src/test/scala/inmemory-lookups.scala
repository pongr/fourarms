package com.pongr.fourarms.matcher

class InmemoryRejectLookup extends Lookup {

  val emails = List("spammer@test.com", "spam@test.com", "nogood@test.com")

  def exist_?(e: String) = emails contains e

}


class InmemoryRelayLookup extends Lookup {

  val emails = List("important@test.com")

  def exist_?(e: String) = emails contains e

}

class InmemoryRejectDomainLookup extends Lookup {

  val domains = List("wearespammer.com", "tom.com")

  def exist_?(e: String) = domains contains e

}
