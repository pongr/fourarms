# Fourarms

Fourarms provides a collection of useful mailets and matchers.

## sbt

```scala
// Fourarms is available at https://oss.sonatype.org/
val fourarms = "com.pongr" %% "fourarms" % "0.1-SNAPSHOT"
```

## Usage

### Matchers

* SenderIsInFile
  ```xml
  <mailet match="SenderIsInLookup=org.domain.SenderLookup" class="ToProcessor">
      <processor>reject</processor>
  </mailet>
```

* RecipientIsInLookup
  ```xml
  <mailet match="RecipientIsInLookup=org.domain.RecipientLookup" class="ToProcessor">
      <processor>reject</processor>
  </mailet>
  ```

* DomainIsInFile
  ```xml
  <mailet match="DomainIsInLookup=org.domain.DomainLookup" class="ToProcessor">
      <processor>reject</processor>
  </mailet>
  ```

SenderLookup, RecipientLookup and DomainLookup classes have to implement com.pongr.fourarms.matcher.SimpleDbLookup trait.

### Mailets

* Rabbit AMPQ mailet
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

* Meter mailet
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
  ```xml
  <mailet match="All" class="com.pongr.fourarms.mailet.GraphiteReporterMailet">
      <host>domU-12-31-39-16-BC-B7.compute-1.internal</host>
      <port>2003</port>
      <period>1</period>
      <timeUnit>minutes</timeUnit>
  </mailet>
  ```

* ChangeRecipient mailet
  ```xml
  <mailet match="All" class="com.pongr.fourarms.mailet.ChangeRecipient">
      <oldRecipient>x@fourarms.pongrdev.com</oldRecipient>
      <newRecipient>y@fourarms.pongrdev.com</newRecipient>
  </mailet>
  ```

* ChangeRecipientDomain
  ```xml
  <mailet match="All" class="com.pongr.fourarms.mailet.ChangeRecipientDomain">
      <oldDomain>fourarm.pongrdev.com</oldDomain>
      <newDomain>fourarms.pongrdev.com</newDomain>
  </mailet>
  ```
