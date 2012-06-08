package com.pongr.fourarms.matcher

import com.amazonaws.services.simpledb.AmazonSimpleDBClient
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.simpledb.model.Item

import com.pongr.fourarms.simpledb._
import com.pongr.fourarms.simpledb.Implicits._

trait SimpledbCred {

  def accessKeyId: String
  def secretAccessKey: String
  def domain: String
  def attribute: String

}

class SimpledbLookup(condition: String) extends Lookup with SimpleDBQuery {

  val credClassName = condition.split(",").last

  // Creating an instance of the email lookup
  val creds = Class.forName(credClassName).newInstance.asInstanceOf[SimpledbCred]
  val sdb = new AmazonSimpleDBClient(new BasicAWSCredentials(creds.accessKeyId, creds.secretAccessKey))

  def query = "select * from %s where %s" format (creds.domain, creds.attribute)
  def itemToString(item: Item): String = item(creds.attribute) getOrElse ""
  def items: Seq[String] = selectMany(sdb, true) (itemToString) (query)

  def exist_?(e: String) = items contains e

}
