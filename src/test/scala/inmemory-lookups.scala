package com.pongr.fourarms.matcher

class RejectLookup(condition: String) extends Lookup {

  val emails = List("spammer@test.com", "spam@test.com", "nogood@test.com")

  def exist_?(e: String) = {
    println("---------------------------------------------------")
    println(e)
    println(emails)
    println("---------------------------------------------------")
    emails contains e
  }

}


class RelayLookup(condition: String) extends Lookup {

  val emails = List("important@test.com")

  def exist_?(e: String) = emails contains e

}

class RejectDomainLookup(condition: String) extends Lookup {

  val domains = List("test.com", "tom.com")

  def exist_?(e: String) = domains contains e

}
