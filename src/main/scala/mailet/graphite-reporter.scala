package com.pongr.fourarms.mailet

import org.apache.mailet._
import com.yammer.metrics.reporting.GraphiteReporter

import java.util.concurrent.TimeUnit

class GraphiteReporterMailet extends PongrMailet {

  lazy val host = getInitParameter("host")
  lazy val port = getInitParameter("port", "2003").toInt
  lazy val period = getInitParameter("period", "1").toInt
  lazy val timeUnit = getInitParameter("timeUnit", "minutes") match {
    case "minutes"      => TimeUnit.MINUTES
    case "milliseconds" => TimeUnit.MILLISECONDS
    case _              => TimeUnit.SECONDS
  }

  override def init() {
    GraphiteReporter.enable(period, timeUnit, host, port)
  }

  override def service(mail: Mail) {
    // do nothing
  }

}

import com.yammer.metrics.reporting.ConsoleReporter

/** Enables [[http://metrics.codahale.com/maven/apidocs/com/yammer/metrics/reporting/ConsoleReporter.html ConsoleReporter]] in the init() method, 
  * using period and timeUnit parameters. Ignores all mail in the service() method.
  * 
  * {{{
  * <mailet match="All" class="com.pongr.fourarms.mailet.ConsoleReporterMailet>
  *   <period>10</period>
  *   <timeUnit>seconds</timeUnit>
  * </mailet>
  * }}}
  */
class ConsoleReporterMailet extends PongrMailet {
  lazy val period = getInitParameter("period", "10").toInt
  lazy val timeUnit = getInitParameter("timeUnit", "seconds") match {
    case "minutes"      => TimeUnit.MINUTES
    case "milliseconds" => TimeUnit.MILLISECONDS
    case _              => TimeUnit.SECONDS
  }
  
  override def init() {
    ConsoleReporter.enable(period, timeUnit)
  }
  
  override def service(mail: Mail) {
    //do nothing
  }
}

