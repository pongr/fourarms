package com.pongr.fourarms.matcher

import com.amazonaws.services.simpledb.AmazonSimpleDBClient
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.simpledb.AmazonSimpleDB
import com.amazonaws.services.simpledb.model.{SelectRequest, Item}

import scala.collection.JavaConversions._

trait SimpleDbLookup extends Lookup {

  // SimpleDB credentials
  def accessKeyId: String
  def secretAccessKey: String
  def domain: String
  def attribute: String

  // Creating an instance of the email lookup
  lazy val sdb = new AmazonSimpleDBClient(new BasicAWSCredentials(accessKeyId, secretAccessKey))

  def query(value: String) = "select count(*) from %s where %s='%s'" format (domain, attribute, value)

  def exist_?(e: String) = {
    val selectRequest = new SelectRequest(query(e))
    sdb.select(selectRequest).getItems.headOption map { item =>
      // Item = {Name: Domain, Attributes: [{Name: Count, Value: 1, }], }
      item.getAttributes.headOption.map { _.getValue != "0" } getOrElse false
    } getOrElse false
  }

}
