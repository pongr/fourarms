# Fourarms

Fourarms is a small project based on [Apache James](http://james.apache.org), provides a collection of useful mailets and matchers.

## sbt

```scala
// Fourarms is available at https://oss.sonatype.org/
val fourarms = "com.pongr" %% "fourarms" % "0.1-SNAPSHOT"
```

## Usage

### Matchers

* SenderIsInLookup

  If the sender of a received email exist in the provided lookup transfers the email to the processor provided with parameter.
  ```xml
  <mailet match="com.pongr.fourarms.matcher.SenderIsInLookup=org.domain.SpamSenderLookup" class="ToProcessor">
      <processor>reject</processor>
  </mailet>
  ```

  ```scala
  package org.domain

  import com.pongr.fourarms.matcher.Lookup
  class SpamSenderLookup extends Lookup {
    val emails = List("spammer@test.com", "spam@test.com", "nogood@test.com")
    def exist_?(e: String) =  contains e
  }
  ```

* RecipientIsInLookup

  Similiar to SenderIsInLookup but tests the recipient of received emails.
  ```xml
  <mailet match="com.pongr.fourarms.matcher.RecipientIsInLookup=org.domain.RecipientLookup" class="ToProcessor">
      <processor>relay</processor>
  </mailet>
  ```

* DomainIsInLookup

  Similiar to SenderIsInLookup but tests the domain of received emails.
  ```xml
  <mailet match="com.pongr.fourarms.matcher.DomainIsInLookup=org.domain.SpamDomainLookup" class="ToProcessor">
      <processor>reject</processor>
  </mailet>
  ```

Note that SpamSenderLookup, RecipientLookup and SpamDomainLookup classes have to implement com.pongr.fourarms.matcher.Lookup trait.

### Mailets

* Rabbit AMPQ mailet

  Serializes received emails and send over RabbitMQ.
  ```xml
  <mailet match="All" class="com.pongr.fourarms.mailet.AmqpMailet">
      <serializer>com.pongr.fourarms.serializer.DefaultSerializer</serializer>
      <host>rabbitmq.pongrdev.com</host>
      <port>5672</port>
      <vhost>test</vhost>
      <username>testuser</username>
      <password>pass</password>
      <exchange>test-exchange</exchange>
      <exchangeType>direct</exchangeType>
      <routing-key>routingKey</routing-key>
      <ghost>true</ghost>
  </mailet>
  ```
  Note that the exchange is durable.

* Meter mailet

  Measures various received email rates. Creates [Yammer Meter] (http://metrics.codahale.com/maven/apidocs/com/yammer/metrics/core/Meter.html) and calls mark() in its service method. 
  ```xml
  <mailet match="All" class="com.pongr.fourarms.mailet.MeterMailet">
      <group>com.pongr</group>
      <type>james</type>
      <name>queue</name>
      <scope>x</scope>
      <eventType>queued-emails</eventType>
      <timeUnit>minutes</timeUnit>
  </mailet>
  ```

* GraphiteReporter mailet

  Enables [metrics-graphite](http://metrics.codahale.com/manual/graphite/) module to report to the provided server.
  ```xml
  <mailet match="All" class="com.pongr.fourarms.mailet.GraphiteReporterMailet">
      <host>domU-12-31-39-16-BC-B7.compute-1.internal</host>
      <port>2003</port>
      <period>1</period>
      <timeUnit>minutes</timeUnit>
  </mailet>
  ```
  Default values: port=2003, period=1, timeUnit=minutes

* ChangeRecipient mailet

  Rewrites x@pongr.com to y@pongr.com before sending to web service.
  ```xml
  <mailet match="All" class="com.pongr.fourarms.mailet.ChangeRecipient">
      <oldRecipient>x@fourarms.pongrdev.com</oldRecipient>
      <newRecipient>y@fourarms.pongrdev.com</newRecipient>
  </mailet>
  ```

* ChangeRecipientDomain

  Rewrites old.com to new.com before sending to web service.
  ```xml
  <mailet match="All" class="com.pongr.fourarms.mailet.ChangeRecipientDomain">
      <oldDomain>fourarm.pongrdev.com</oldDomain>
      <newDomain>fourarms.pongrdev.com</newDomain>
  </mailet>
  ```

## License

Grey Matter is licensed under the [Apache 2 License](http://www.apache.org/licenses/LICENSE-2.0.txt).
