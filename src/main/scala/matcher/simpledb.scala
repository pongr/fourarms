package com.pongr.fourarms.matcher

trait SimpledbCred {

  def accessKeyId: String
  def secretAccessKey: String
  def domain: String
  def attribute: String

}

class SimpledbLookup(condition: String) extends Lookup {

  val credClassName = condition.split(",").last

  // Creating an instance of the email lookup
  lazy val cred = Class.forName(credClassName).newInstance.asInstanceOf[SimpledbCred]

  def exist_?(e: String) = {
    true
  }

}
