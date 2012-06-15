# Fourarms

Fourarms is a small project based on [Apache James](http://james.apache.org), provides a collection of useful mailets and matchers.

## Install

### sbt 
```scala
// Fourarms is available at https://oss.sonatype.org/
"com.pongr" %% "fourarms" % "0.1-SNAPSHOT"
```

### Maven

```xml
<dependency>
    <groupId>com.pongr</groupId>
    <artifactId>fourarms_2.9.1</artifactId>
    <version>0.1-SNAPSHOT</version>
</dependency>
```

### Matchers

* SenderIsInLookup

  If the sender of a received email exists it transfers the email to the processor provided by the parameter. It uses Lookup trait to to check if the sender is in the lookup. So you'll have to provide an implementation of Lookup trait.

  Lookup trait is simple as:
  ```scala
  trait Lookup {
      def exist_?(element: String): Boolean
  }
  ```
  A simple way to implement Lookup trait is hard-coding the emails.

  ```scala
  package org.domain

  import com.pongr.fourarms.matcher.Lookup
  class HardCodedSpamSenderLookup extends Lookup {
      val emails = List("spammer@test.com", "spam@test.com")
      def exist_?(e: String) =  contains e
  }
  ```

  And in James xml confuguration it would go like:
  ```xml
  <mailet match="com.pongr.fourarms.matcher.SenderIsInLookup=org.domain.HardCodedSpamSenderLookup" class="ToProcessor">
      <processor>reject</processor>
  </mailet>
  ```
  Fourarms also has SimpleDB lookup trait that extends Lookup trait. Here is an usage of that:

  ```scala
  class SimpleDbSpamSenderLookup extends SimpleDbLookup {
    val accessKeyId = "simpledb access key id"
    val secretAccessKey = "simpledb secret access key"
    val domain = "simple domain"
    val attribute = "spammerEmail"
  }
  ```

  James xml configuration:
  ```xml
  <mailet match="com.pongr.fourarms.matcher.SenderIsInLookup=org.domain.SimpleDbSpamSenderLookup" class="ToProcessor">
      <processor>reject</processor>
  </mailet>


* RecipientIsInLookup

  Similiar to SenderIsInLookup but tests the recipient of received emails.
  ```xml
  <mailet match="com.pongr.fourarms.matcher.RecipientIsInLookup=org.domain.RecipientLookup" class="ToProcessor">
      <processor>relay</processor>
  </mailet>
  ```

* SenderDomainIsInLookup

  Similiar to SenderIsInLookup but tests the sender domain of received emails. Let's say we have an email sent from **sender@domain.com**. SenderDomainIsInLookup will test **domain.com** against the lookup where SenderIsInLookup would test **sender@domain.com**.

  ```xml
  <mailet match="com.pongr.fourarms.matcher.SenderDomainIsInLookup=org.domain.SpamSenderDomainLookup" class="ToProcessor">
      <processor>reject</processor>
  </mailet>
  ```

### Mailets

* AMPQ mailet

  Serializes received emails and send over AMQP server.
  ```xml
  <mailet match="All" class="com.pongr.fourarms.mailet.AmqpMailet">
      <serializer>com.pongr.fourarms.serializer.DefaultSerializer</serializer>
      <host>amqp-server.domain.org</host>
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

  Enables [metrics-graphite](http://metrics.codahale.com/manual/graphite/) module to report to the graphite server defined by the parameters.
  ```xml
  <mailet match="All" class="com.pongr.fourarms.mailet.GraphiteReporterMailet">
      <host>graphite.domain.org</host>
      <port>2003</port>
      <period>1</period>
      <timeUnit>minutes</timeUnit>
  </mailet>
  ```
  Default values: port=2003, period=1, timeUnit=minutes

* ChangeRecipient mailet

  Changes a matching recipient on the Mail object, so that matchers and mailets further down the processing chain will see the new recipient address instead of old one.
  ```xml
  <mailet match="All" class="com.pongr.fourarms.mailet.ChangeRecipient">
      <oldRecipient>x@domain.org</oldRecipient>
      <newRecipient>y@domain.org</newRecipient>
  </mailet>
  ```

* ChangeRecipientDomain

  Changes a matching recipient domain of received email before sending.
  ```xml
  <mailet match="All" class="com.pongr.fourarms.mailet.ChangeRecipientDomain">
      <oldDomain>fourarm.domain.org</oldDomain>
      <newDomain>fourarms.domain.org</newDomain>
  </mailet>
  ```

## License

Fourarms is licensed under the [Apache 2 License](http://www.apache.org/licenses/LICENSE-2.0.txt).
