package com.pongr.fourarms.matcher

import com.amazonaws.services.simpledb.AmazonSimpleDBClient
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.simpledb.model.Item

import com.pongr.fourarms.simpledb._
import com.pongr.fourarms.simpledb.Implicits._

trait SimpleDbLookup extends Lookup with SimpleDbQuery {

  // SimpleDB credentials
  def accessKeyId: String
  def secretAccessKey: String
  def domain: String
  def attribute: String

  // Creating an instance of the email lookup
  lazy val sdb = new AmazonSimpleDBClient(new BasicAWSCredentials(accessKeyId, secretAccessKey))

  def query = "select * from %s where %s is not null" format (domain, attribute)
  def itemToString(item: Item): String = item(attribute) getOrElse ""
  def items: Seq[String] = selectMany(sdb) (itemToString) (query)

  def exist_?(e: String) = items contains e

}
