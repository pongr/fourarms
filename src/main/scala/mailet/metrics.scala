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

