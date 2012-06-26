package com.pongr.fourarms.serializer

import org.specs2.mutable._
import org.apache.commons.io.IOUtils
import java.io._

import com.pongr.fourarms.mail._
import com.pongr.fourarms.Helper

class SerializerSpec extends Specification with Helper {

  def serializer = new DefaultSerializer

  def isEqual(m1: Email, m2: Email): Boolean = {
    def isPartsEqual = m1.parts.zipWithIndex.foldLeft(true) { (res, p1) =>
      val i1 = new ByteArrayInputStream(p1._1.data)
      val i2 = new ByteArrayInputStream(m2.parts(p1._2).data)
      IOUtils.contentEquals(i1, i2) && res
    }

    m1.from          == m2.from &&
    m1.to            == m2.to &&
    m1.subject       == m2.subject &&
    m1.headers       == m2.headers &&
    m1.remoteHost    == m2.remoteHost &&
    m1.remoteAddr    == m2.remoteAddr &&
    m1.parts.size    == m2.parts.size && isPartsEqual

  }

  "SerializerSpec test" should {

    "serialize/deserialize mail object" in {

      val files = Seq("/MimeMessage-plain-jpeg-multipleRecipient", 
                      "/MimeMessage-plain", 
                      "/MimeMessage-plain-jpeg", 
                      "/MimeMessage-plain-html")

      val testResult = files.foldLeft(true) { (res, fileName) =>
        val m1 = Email(createMail(fileName))
        val bytes = serializer.serialize(m1)
        val m2 = serializer.deserialize(bytes)
        isEqual(m1, m2) && res
      }

      testResult must_== true

    }

  }

}
