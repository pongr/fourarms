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
import com.yammer.metrics.reporting.GraphiteReporter

class GraphiteReporterMailet extends PongrMailet {

  lazy val host = getInitParameter("host")
  lazy val port = getInitParameter("port", "2003").toInt
  lazy val period = getInitParameter("period", "1").toInt
  lazy val timeUnit = getTimeUnit(getInitParameter("timeUnit", "minutes"))

  override def init() {
    GraphiteReporter.enable(period, timeUnit, host, port)
    log("Enabled GraphiteReporter to report to %s:%d every %d %s" format (host, port, period, timeUnit))
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
  * <mailet match="All" class="com.pongr.fourarms.mailet.ConsoleReporterMailet">
  *   <period>10</period>
  *   <timeUnit>seconds</timeUnit>
  * </mailet>
  * }}}
  */
class ConsoleReporterMailet extends PongrMailet {
  lazy val period = getInitParameter("period", "10").toInt
  lazy val timeUnit = getTimeUnit(getInitParameter("timeUnit", "seconds"))
  
  override def init() {
    ConsoleReporter.enable(period, timeUnit)
    log("Enabled ConsoleReporter to report every %d %s" format (period, timeUnit))
  }
  
  override def service(mail: Mail) {
    //do nothing
  }
}

