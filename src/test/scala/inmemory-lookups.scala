package com.pongr.fourarms.matcher

class InmemoryRejectLookup(condition: String) extends Lookup {

  val emails = List("spammer@test.com", "spam@test.com", "nogood@test.com")

  def exist_?(e: String) = emails contains e

}


class InmemoryRelayLookup(condition: String) extends Lookup {

  val emails = List("important@test.com")

  def exist_?(e: String) = emails contains e

}

class InmemoryRejectDomainLookup(condition: String) extends Lookup {

  val domains = List("test.com", "tom.com")

  def exist_?(e: String) = domains contains e

}
