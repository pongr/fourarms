package com.pongr.fourarms.simpledb

import com.amazonaws.services.simpledb.AmazonSimpleDB
import com.amazonaws.services.simpledb.model.{PutAttributesRequest, SelectRequest, ReplaceableAttribute, Item}
import scala.collection.JavaConversions._

object Implicits {
  
  implicit def asOption[T](item: Option[Item]): Option[T] = item map { Some(_) } getOrElse None

  class RichItem(item: Item) {
    def apply(name: String): Option[String] = values(name) headOption
    def values(name: String): List[String] = item.getAttributes filter { name == _.getName } map { _.getValue } toList
  }

  implicit def itemToRichItem(item: Item): RichItem = new RichItem(item)
  implicit def pairToReplaceableAttribute[A, B](p: (A, B)) = new ReplaceableAttribute(p._1.toString, p._2.toString, false)

}
