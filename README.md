# Fourarms

A collection of Scala-based mailets and matchers for Apache James [Apache James](http://james.apache.org).

## Install

### sbt 
```scala
"com.pongr" %% "fourarms" % "0.1"
```

### Maven

```xml
<dependency>
    <groupId>com.pongr</groupId>
    <artifactId>fourarms_2.9.1</artifactId>
    <version>0.1-SNAPSHOT</version>
</dependency>
```

## Usage

### Matchers

#### Lookups

We've found many cases where we want to process a mail with a specific mailet only when some attribute of that mail is contained in some collection. For example, ignoring a mail if the sender is in a list of known spammer addresses, or relaying the mail to another mail server if the recipient is in some special list.

Fourarms provides the Lookup trait that abstracts away the specifics of storing and querying this collection:

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
  def exist_?(e: String) = emails contains e
}
```

Fourarms also has SimpleDB lookup trait that extends Lookup trait. You just extend SimpleDbLookup and provide several values. To lookup the value, it executes a query like ```select count(*) from <domain> where <attribute>=<value>```. Here is an example usage of it:

```scala
class SimpleDbSpamSenderLookup extends SimpleDbLookup {
  val accessKeyId = "simpledb access key id"
  val secretAccessKey = "simpledb secret access key"
  val domain = "YourSpammerDomain"
  val attribute = "spammerEmail"
}
```

Fourarms then provides several matchers that test if a specific mail attribute is contained in a Lookup implementation. These are documented below. In each case, the Lookup implementation is provided as the matcher's config parameter (after the equal sign) such as:

```xml
<mailet match="com.pongr.fourarms.matcher.SenderIsInLookup=org.domain.HardCodedSpamSenderLookup" class="ToProcessor">
  <processor>reject</processor>
</mailet>
```

#### SenderIsInLookup

Matches if the sender of a received email exists in the Lookup. So you'll have to provide an implementation of Lookup trait as the matcher's config parameter (after the equal sign).
  
```xml
<mailet match="com.pongr.fourarms.matcher.SenderIsInLookup=org.domain.SimpleDbSpamSenderLookup" class="ToProcessor">
  <processor>reject</processor>
</mailet>
```

#### RecipientIsInLookup

Similiar to SenderIsInLookup but tests the recipient of received emails.

```xml
<mailet match="com.pongr.fourarms.matcher.RecipientIsInLookup=org.domain.RecipientLookup" class="ToProcessor">
  <processor>relay</processor>
</mailet>
```

#### SenderDomainIsInLookup

Similiar to SenderIsInLookup but tests the sender domain of received emails. Let's say we have an email sent from sender@domain.com. SenderDomainIsInLookup will test **domain.com** against the lookup where SenderIsInLookup would test sender@domain.com.

```xml
<mailet match="com.pongr.fourarms.matcher.SenderDomainIsInLookup=org.domain.SpamSenderDomainLookup" class="ToProcessor">
  <processor>reject</processor>
</mailet>
```

### Mailets

#### AmqpMailet

Serializes received emails and sends to AMQP server.

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

TODO documentation for Serializer and DefaultSerializer.

#### MeterMailet

Makes it easy to measure various received email rates. Creates [Yammer Meter] (http://metrics.codahale.com/maven/apidocs/com/yammer/metrics/core/Meter.html) and calls mark() in its service method. 

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

#### GraphiteReporterMailet

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

#### ChangeRecipient

Changes a matching recipient on the Mail object, so that matchers and mailets further down the processing chain will see the new recipient address instead of old one.

```xml
<mailet match="All" class="com.pongr.fourarms.mailet.ChangeRecipient">
  <oldRecipient>x@domain.org</oldRecipient>
  <newRecipient>y@domain.org</newRecipient>
</mailet>
```

#### ChangeRecipientDomain

Changes a matching recipient domain on the Mail object, so that matchers and mailets further down the processing chain will see the new recipient address instead of old one.

```xml
<mailet match="All" class="com.pongr.fourarms.mailet.ChangeRecipientDomain">
  <oldDomain>domain1.org</oldDomain>
  <newDomain>domain2.org</newDomain>
</mailet>
```

## License

Fourarms is licensed under the [Apache 2 License](http://www.apache.org/licenses/LICENSE-2.0.txt).
