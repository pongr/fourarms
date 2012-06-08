package com.pongr.fourarms.simpledb

import com.amazonaws.services.simpledb.AmazonSimpleDB
import com.amazonaws.services.simpledb.model.{SelectRequest, Item}
import scala.collection.JavaConversions._

trait SimpleDBQuery {
  
  def selectOne[T] (sdb: AmazonSimpleDB, isConsistent: Boolean = false) (converter: (Item => T)) (query: String) = {
    selectMany(sdb, isConsistent)(converter)(query) headOption
  }

  def selectMany[T] (sdb: AmazonSimpleDB, isConsistent: Boolean = false) (converter: (Item => T)) (query: String) = {
    val selectRequest = (new SelectRequest(query)).withConsistentRead(isConsistent)
    sdb.select(selectRequest).getItems map (converter (_))
  }

}
