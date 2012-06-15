/*
 * Copyright (c) 2012 Pongr, Inc.
 *   
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pongr.fourarms.matcher

import com.amazonaws.services.simpledb.AmazonSimpleDBClient
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.simpledb.AmazonSimpleDB
import com.amazonaws.services.simpledb.model.{SelectRequest, Item}

import java.util.concurrent.TimeUnit
import com.yammer.metrics.Metrics

import scala.collection.JavaConversions._

trait SimpleDbLookup extends Lookup {

  // SimpleDB credentials
  def accessKeyId: String
  def secretAccessKey: String
  def domain: String
  def attribute: String

  def timerName = "SimpleDbLookup"
  def durationUnit = TimeUnit.MILLISECONDS
  def rateUnit = TimeUnit.SECONDS

  // Creating an instance of the email lookup
  lazy val sdb = new AmazonSimpleDBClient(new BasicAWSCredentials(accessKeyId, secretAccessKey))

  def query(value: String) = "select count(*) from %s where %s='%s'" format (domain, attribute, value)

  def exist_?(e: String) = {

    val timer = Metrics.newTimer(this.getClass, timerName, durationUnit, rateUnit)
    val context = timer.time()

    try {
      val selectRequest = new SelectRequest(query(e))
      sdb.select(selectRequest).getItems.headOption map { item =>
        // Item = {Name: Domain, Attributes: [{Name: Count, Value: 1, }], }
        item.getAttributes.headOption.map { _.getValue != "0" } getOrElse false
      } getOrElse false
    }
    finally {
      context.stop()
    }
  }

}
