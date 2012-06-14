package com.pongr.fourarms.mailet

import org.apache.mailet._
import com.yammer.metrics.Metrics
import com.yammer.metrics.core.MetricName

import java.util.concurrent.TimeUnit

class MeterMailet extends PongrMailet {

  lazy val group = getInitParameter("group")
  lazy val metricType = getInitParameter("type")
  lazy val name = getInitParameter("name")
  lazy val scope = getInitParameter("scope")

  lazy val eventType = getInitParameter("eventType")
  
  lazy val timeUnit = getTimeUnit(getInitParameter("timeUnit", "minutes"))

  lazy val metricName = new MetricName(group, metricType, name, scope)
  lazy val meter = Metrics.newMeter(metricName, eventType, timeUnit)
  
  override def service(mail: Mail) {
    meter.mark()
  }

}

