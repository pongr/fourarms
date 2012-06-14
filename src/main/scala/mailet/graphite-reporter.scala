package com.pongr.fourarms.mailet

import org.apache.mailet._
import com.yammer.metrics.reporting.GraphiteReporter

class GraphiteReporterMailet extends PongrMailet {

  lazy val host = getInitParameter("host")
  lazy val port = getInitParameter("port", "2003").toInt
  lazy val period = getInitParameter("period", "1").toInt
  lazy val timeUnit = getTimeUnit(getInitParameter("timeUnit", "minutes"))

  override def init() {
    GraphiteReporter.enable(period, timeUnit, host, port)
  }

  override def service(mail: Mail) {
    // do nothing
  }

}

