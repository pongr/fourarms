package com.pongr.fourarms.mailet

import org.apache.mailet._
import java.io._
import org.apache.commons.io._

/** Serializes every mail's MimeMessage using its writeTo() method to a temporary file and logs that file's absolute path.
  * Useful for capturing the MimeMessage's full content for later use. Usage would be like:
  * 
  * {{{
  * <mailet match="All" class="com.pongr.fourarms.mailet.SerializeMimeMessage">
  *   <prefix>MimeMessage</prefix>
  * </mailet>
  * }}}
  */
class SerializeMimeMessage extends PongrMailet {
  /** Prefix of the temporary file. Required. See java.io.File.createTempFile for details. */
  lazy val prefix = getInitParameter("prefix")
  
  /** Suffix of the temporary file. Omit to use default. See java.io.File.createTempFile for details. */
  lazy val suffix = getInitParameter("suffix")
  
  /** Directory of the temporary file. Omit to use default. See java.io.File.createTempFile for details. */
  lazy val directory = new File(getInitParameter("directory"))

  override def service(mail: Mail) {
    try {
      val file = File.createTempFile(prefix, suffix, directory)
      val os = new FileOutputStream(file)
      mail.getMessage.writeTo(os)
      os.close()
      log("Serialized mail %s to %s" format (mail.getName, file))
    } catch {
      case t: Throwable => log("Error trying to serialize MimeMessage: " + t.getMessage)
    }
  }
}
