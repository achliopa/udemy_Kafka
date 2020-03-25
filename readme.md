# Udemy Course: Apache Kafka Series - Learn Apache Kafka for Beginners v2

* [Course Link](https://www.udemy.com/course/apache-kafka/)
* [Course Repo](https://github.com/simplesteph/kafka-beginners-course)
* [Lecturers Website](https://courses.datacumulus.com/)

## Section 1: Kafka Introduction

### Lecture 2. Apache Kafka in 5 minutes

* If we have 4 source systems and 6 target systems all exchanging data with each other we need to implement 24 APIs, for each we need to choose
    * Protocol (TCP,HTTP,REST,JDBC)
    * Data Format (CSV,XML,JSON)
    * Data Schema & Evolution (how data is shaped, versions)
* Each source system will have a high load from the 6 connecitons
* Apache Kafka is a Middleware that Decouples Data Streams & Systems
* it acts as a hub. data can come from asy source and go in any target
* Why Kafka?
    * Created by LinkedIn, now OpenSource mainly maintained by Confluent
    * Distributed, resilient, fault tolerant architecture
    * It scales horizontaly: can scale to 100s of brokers, millions of messages/second
    * High Performance (latency of <10ms) - real time
    * 2000+ firms, 35% of Fortune500 use it
* Use cases:
    * messaging system
    * activity tracking
    * gather metrics from different locations (IoT)
    * applications log gathering
    * stream processing (with Kafka Streams API or Spark)
    * Decoupling of system dependencies
    * integration with Spark,Flink,Storm,Hadoop,and many other BigData technologies
* Examples:
    * Netflix uses kafka to apply recommendations in real-time while we watch tv shows
    * Uber uses Kafka to gather  user,taxi and trip data in real-time to compute and forecast demand, and compute surge pricing in real-time
    * Linkedin used kafka to prevent spamming, colect user interaction to make better connection recommendations in real time
* Kafka only middleware

### Lecture 3. Course Objectives

* Part 1 - Fundamentals
    * Kafka Theory
    * Starting kafka
    * Kafka CLI
    * Kafka & Java
* Part 2 - Real World Example
    * Twitter Producer
    * ElasticSearch Consumer
    * Extended API intro + Case Study + Kafka in Enterprise
* Part 3 - Advanced & Annexes
    * Advanced Topic Configuration
    * Annexes

## Section 3: ====== Kafka Fundamentals ======

### Lecture 6. Kafka Fundamentals

* In this first learning block, we are going through the Kafka 101.
    * We'll have a very necessary all-theory section, during which we'll learn all the fundamentals of Kafka
    * We'll then go ahead and setup Kafka on our computers
    * We'll spend a lot of time learning how to use the Kafka CLI
    * We'll write some Java Code to create our first Producers and Consumers

## Section 4: Kafka Theory

### Lecture 8. Topics, Partitions and Offsets

* Topics: A particular stream of data
    * similar to a table in a database (without all the constraints)
    * we can have as many topics as we want
    * a topic is identified by its name
* Topics are split in partitions (when we create a topic we specify how many partitions, the number can change later on)
    * each partitions is ordered
    * each message within a partition gets an incremental id, called offset which is unbounded
* Number of messages in partitions is independent from each other
* Example. we have trucks reporting their gps position to Kafka. we use a topic "trucks_gps" containing the position of all trucks
* each truck will report position every 20secs (each message will contain truck ID and truck position(lat,lng) )
* we choose to create that topic with 10 partitions (arbitrary num)
* kafka will have consumenrs a location dashboard and a notification service
* Gotchas:
    * Offset will have meaning only for a specific partition (e.g offset 2 in partition 0 does not represent the same data as offset 3 partition 1)
    * order is guaranteed only within a partition (not accross partitions)
    * Data is kept only for limited time (default is 1 week) (offsets will keep incrementing, they are not reset when data is deleted)
    * once data is written to a partition. it cant be changed (immutability)
    * data is assigned randomly to a partition unless a key is provided

### Lecture 9. Brokers and Topics

* A Kafka cluster is composed of multiple brokers (servers)
* Each Broker is identified by its ID (integer) e.g "Broker 101"
* Each broker contains certain topic partitions
* After connecting to any broker (called a bootstrap broker) we will be connected to the entire cluster
* A good starting number is 3 brokers, but some big clusters have over 100 brokers
* e.g we can choose to number brokers starting at 100 (arbitrary) e.g "Broker 101" "Broker 102" "Broker 103"
    * A topic with 3 partitions will be evenly distributed among 3 brokers (each partition in 1 broker)
    * A topic with 2 partitions will go to 2 out of 3 brokers

### Lecture 10. Topic Replication

* Topics should have a replication factor >1 (usually between 2 and 3)
* this way if a broker is down,another broker can server data
* e.g a Topic with 2 partitions and replication factor of 2. 
    * total 2 + 2 partitions to place
    * partion placement rules apply
    * a partition and its replica cannot be placed in the same broker
    * Broker 101 [TopicA Part0], Broker 102[TopicA Part1, TopicA Part0repl], Broker103[TopicA Part1repl]
    * If Broker102 goes out everything is OK, data is not lost
* Leader of Partition
    * At any time only ONE broker can be a leader for a given partition
    * only the leader can receive and server data for the partition
    * the other brokers will just synchronize their data
    * each parttion has one leader and multiple ISR (in-sync replica)

### Lecture 11. Producers and Message Keys

* Producers (data input)
    * Producers write data to topics (which is made of partitions)
    * Producers automatically know to which broker and partition to write to
    * In case of Broker failures, Producers will automatically recover
* When producer sends data, the load is balanced to many brokers thanks to the number of topic partitions
* If data do not have a key the load balancing is based on round robin
* Producers can choose to receive acknowledgment of data writes
    * acks=0: Producer won't wait for acknowledgment (possible data loss)
    * acks=1: Producer will wait for leader acknowledgment (limited data loss)
    * acks=all: Leader + replicas acknowledgment (no data loss)
* Message Keys:
    * roducers can choose to send a key with the message (string,number etc)
    * if key=null, data is sent round robin (broker 101=>102=>103)
    * if a key is sent, then data for that key will always go to the same partition (but to which is arbitrary)
    * a key is sent if we need message ordering for a specific filed (e.g truck_id) as only in partition there is guaranteed ordering. 
    * we get this guarantee thanks to key hashing, which depends on the num of paritions

### Lecture 12. Consumers & Consumer Groups

* consumers read data from a topic (identified by name)
* consumers know which broker to read from
* in case of broker failures, consumers know how to recover
* data is read in order *withinn each partitions*
* if the topic is partitioned across multiple brokers  order of data across partitions is not guaranteed
* Consumer Groups
    * consumers read data in consumer groups
    * each consumer within a group will read from exclusive partitions
    * if we have more consumers that partitions, some consumers will be inactive
* example: if a consumer group app has 2 consumers and the topic has 3 partitions, one consumer will read from first 2 partitions and 2nd consumer from 3rd. if we have a second consumer group with 3 consumers for this group each consumer will read from one partition. if we have a 3rd group with 1 consumer it will read from all 3 partitions. if we have 3 partitions and 4 consumers.. 3 get 1 partition and 1 is inactive

### Lecture 13. Consumer Offsets & Delivery Semantics

* Kafka stores the offsets at which a consumer group has been reading
* the offsets committed live in a Kafka topic named __consumer_offsets
* when a consumer in a group has procesed data received from kafka, it should be committing the offsets
* If a consumer dies, it will be able to read back from where it left off thanks to the commited consumer offsets
* Commiting offsets requires Delivery Semantics
* consumers choose when to commit offsets
* There are 3 delivery semantics:
    * at most once: offsets are commited as soon as the message is received. if the processing goes wrong, message will be lost (wont be read again)
    * at least once (usually preferred): offsets are commited only after the message is processed. this can result in duplicate processing of messages. make sure our processing is "idempotent" (e.g. processing again the messages wont impact our systems)
    * exactly once: can be achieved for Kafka => Kafka workflows using Kafka Streams API (maybe also with Spark Streaming) For kafka => External System use an "idempotent" consumer

### Lecture 14. Kafka Broker Discovery

* Every Kafka broker is called "bootstrap server"
* This means we only need to connect to one broker only (anyone) and we will be connected to the entire cluster
* Each broker knows about all brokers, topics and partitions (metadata)
* Kafka Client connects to any broker and requests metadata. he gets back a list of all brokers with metadata. then client knows where to connect to produce/consume

### Lecture 15. Zookeeper

* Zookeper manages brokers (keeps a list of them)
* Zookeper helps in performing leader election for partitions
* Zookeper sends notifications to Kafka in case of changes (new topic, broker dies, comes up, delete topics etc)
* Kafka cannot work without Zookeper
* Zookeper by design operated with an odd number of servers
* Zookeper has a leader (handle writes) and the rest of the servers are followers (handle reads)
* Zookeper does NOT store consumer offsets with Kafka  >v0.10. it is isolated from producers and consumers. consumer offsets are stored in the topic
* Zookeper servers are logicaly separated from kafka Brokers, a zookeper server might serve multiple kafka brokers. the concept is a zookeper cluster + kafka cluster
* Zookeper works under the hood. we dont mess with it

### Lecture 16. Kafka Guarantees

* Messages are appended to a topic-partition in the order they are sent
* Consumers read messages in the order stored in a topic-partition
* With a replication factor of N, producers and consumers can tolerate up to N-1 brokers going down
* This is why replication factor of 3 is the Sweet spot:
    * Allows 1 to go down for maintenance
    * Allows 1 to go down unexpectedly
* As long as the number of partitions remains constant for a topic (no new partitions) the same key will always go to the same partition

### Lecture 17. Theory Roundup

* Producers and Consumers are Kafka components. they are logically separated from Source and Target Systems

## Section 5: Starting Kafka

### Lecture 24. Linux - Download and Setup Kafka in PATH

* single machine installation
* we fire up a t2.small EC2 Ubuntu instance on AWS (dont try micro it wont work) it needs at least 2GB of RAM 
* we install java 
```
sudo apt-get update
sudo apt install openjdk-8-jdk
```
* check java version `java -version` it must be 8
* we need to [download kafka](https://kafka.apache.org/downloads). we get latest stable release 2.4.1 binary download with scala 2.12 `wget https://downloads.apache.org/kafka/2.4.1/kafka_2.12-2.4.1.tgz`
* untar it `tar -xvf kafka_2.12-2.4.1.tgz`
* enter kafka dir `cd kafka_2.12-2.4.1`
* to see that kafka works we run `bin/kafka-topics.sh` we should get no errors
* add kafka to PATH. edit .bashrc and add at the end `export PATH=/home/ubuntu/kafka_2.12-2.4.1/bin:$PATH` 
* open a new terminal or `source .bashrc`
* form any path run `kafka-topics.sh` and it should work

### Lecture 25. Linux - Start Zookeeper and Kafka

* in the kafka dir create a data folder `mkdir data` 
* we cd in it and create a zookeper dir `mkdir zookeeper`
* we will edit zookeper properties to use the data/zookeper folder
* in kafka dir `vi config/zookeper.properties`
* set dataDir to `dataDir=/home/ubuntu/kafka_2.12-2.4.1/data/zookeeper`
* being in kafka dir we run `bin/zookeeper-server-start.sh config/zookeeper.properties`
* zookeper server starts. 
* we see from the log that we get a binding at port 2182 `INFO binding to port 0.0.0.0/0.0.0.0:2181`
* we open a new terminal
* we confirm that zookeper is outputing data at /data/zookeper. we see version-2 folder generated there
* we can now start kafka
* we first create a data/kafka dir in kafka installation folder
* we edit kafka props `vi config/server.properties` log.dirs prop `log.dirs=/home/ubuntu/kafka_2.12-2.4.1/data/kafka`
* we also see that in this file many crucual kafka props are set
* we start server `kafka-server-start.sh config/server.properties`
* we checklog and see it starts successfully ` INFO [KafkaServer id=0] started (kafka.server.KafkaServer)`
* we always need 2 terminals as Zookeeper must run when we start  kafka
* on a 3rd terminal we go to /data/kafka and check that kafka is outputing files there

## Section 6: CLI (Command Line Interface) 101

### Lecture 32. Kafka Topics CLI

* with zookeper and kafka server started we can now play with the CLI.
* we run `kafka-topics.sh` to see the options. this is the command for kafka topics (create,delete,describe, change)
* to CREATE a topic we need to name it and reference zookeeper `kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic first_topic --create --partitions 3 --replication-factor 1`
* we cannot put a larger replication-factor than the available brokers
* as of Kafka v2.2 and on we can use `kafka-topics.sh --bootstrap-server localhost:9092 --topic first_topic --create --partitions 3 --replication-factor 1` as zookeepr option is deprecated
* in new Kafka versions --partitions and --replication-factor option is not obligatory
* with `kafka-topics.sh --bootstrap-server localhost:9092 --list` we see all available topics
* to see information about the topic we use `kafka-topics.sh --bootstrap-server localhost:9092 --topic first_topic --describe` what we get back is
```
Topic: first_topic	PartitionCount: 3	ReplicationFactor: 1	Configs: segment.bytes=1073741824
	Topic: first_topic	Partition: 0	Leader: 0	Replicas: 0	Isr: 0
	Topic: first_topic	Partition: 1	Leader: 0	Replicas: 0	Isr: 0
	Topic: first_topic	Partition: 2	Leader: 0	Replicas: 0	Isr: 0

```

* we see that broker 0 is the leader for all partitions
* to delete the topic we use `kafka-topics.sh --bootstrap-server localhost:9092 --topic first_topic --delete`

### Lecture 33. Kafka Console Producer CLI

* `kafka-console-producer.sh` starts a producer that uses the stdin from the terminal as input and publish it to Kafka
* to start it we need to spec the broker-list and the topic `kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic first_topic`
* we get a shell to write wach sentence ended by ENTER is a message published to Kafka
* with ctrl+c we exit
* we can add properties like the acks `kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic first_topic --producer-property acks=all`
* if we run the console producer on a topic that does not exist we get a warning or nothing at all. this is because it gets created automatically... we get a warning on the first messge till a leader is available ut then all is ok (from second mesage)
* if we run topics list comand we see that the topic is created
* this topic is created with defaults... to change the defaults we need to edit the `config/server.properties` file e.g change num.partitions
* this are the defaults we get if we create a topic without properties

### Lecture 34. Kafka Console Consumer CLI

* `kafka-console-consumer.sh` starts a kafka consumer that reads a topic and outputs to stdout
* `kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic first_topic` should do the tric but nothing happens,
* this is because without further options the consumer is listening to the topic for new messages. if we open a new terminal and se the console producer to post on topiv first_topic we will see it in console_consumer
* adding the '--from-beginning' option will purge the topic
* as we can see the order of the messages in this consumer is not absolute.. the order is per partition. because we created the topic with 3 partitions, the absolute ordering is in a signel partition

### Lecture 35. Kafka Consumers in Group

* we want to setup consumer group using the console consumer command using the --group option `kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic first_topic --group my-first-application`
* the consumer works as normal listening to topic
* if i open a new terminal with a cosole consumer in the same group if I publish to the topic i see that some messages go to the first consumer of the group and some to the second
* this is because our topic is prtitioned and the partitions are enough for each consumer group member to attach to one of them. also i see that message is split 2:1 nd this is ok because 2 partitions go to one group member and one to the other
* if i file 3 consumer group members and i publish to the topic i see round robin... as the messages go to partitions in round robin fashion.... an each member is attached to 1 partition
* if i fire up 4 cosumer group members one is silent (idle) because there are not enough partitions for all
* Reasignemtn of patitions is automatic and FASTTTT. AWESOME
* if we fire up a consumer in another froup with the --from-beginning option we see all messages since beginning.... if i do the same in the first group that was purged nothing comes back. this is interesting
* so conumer groups are treated as completely different queues from kafka topic and have their own consumer offsets. this is  why is GOOD offsets to be handled by Kafka

### Lecture 36. Kafka Consumer Groups CLI

* there is a cli command for consumer groups `kafka-consumer-groups.sh`
* its for consumer group management but not creation. it needs the bootstrap-server to work
* to list groups `kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list`
* we can describe with `kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group my-first-application --describe`
* we see alot of info. consumer assignment to partitions. the current offset
* lag info is a health metric. it means how much behind is the consummer offest from the log-end offset.
* if we publish with no consumers listening the lag will increase... if we start again a consumer in the group we get buffered messages and the offsets to match (we dont need --from-beginning as when we dont use groups)

### Lecture 37. Resetting Offsets

* we want to be able to reget data from the topic.
* this can be done by manipulating the offset (reseting)
* we stop the consumers
* we run the `kafka-consumer-groups.sh` command. --reset-offset acepts a lot if options regrding where to place the consumer offset. in ourcase we choose --to-earliest to get them from start 
* we also need to spec the topic and force the reset with --execute `kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group my-first-application --topic first_topic --reset-offsets --to-earliest --execute`
* if i now restart a group consumer i will get all the messages
* instead of --to-earliest we can `--shift-by 2` to move 2 forward . we want to move backwards so we use `--shift-by -2`. we see that all partiitions consumer offset moves 2 messages back
* note we cannot reset-offsets while we have active consumers in the group

### Lecture 38. CLI Options that are good to know

* Producer with keys `kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic first_topic --property parse.key=true --property key.separator=,`
* in the console we must form key,value pairs `key,value`
* Consumer with keys `kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic --from-beginning --property print.key=true --property key.separator=,` which gets `key,value` pairs

### Lecture 39. What about UIs? Conduktor

* Kafka is headless. no gui
* Tutor has made his on [ConduktorUI](https://www.conduktor.io/)

### Lecture 40. KafkaCat as a replacement for Kafka CLI

* [Kafcat](https://github.com/edenhill/kafkacat) is an opensource alternative to CLI
* read [this article](https://medium.com/@coderunner/debugging-with-kafkacat-df7851d21968) before using

## Section 7: Kafka Java Programming 101

### Lecture 42. Installing Java 8 & IntelliJ Community Edition

* we must have Java 8 JDK on our machine
* we install [install IntelliJ Community Edition](https://www.jetbrains.com/idea/download/#section=linux)
* for this section we will work locally as we need to setup SFTP on EC2 instance to connect with Jetbrains to run our project on AWS EC2... not worth it..

### Lecture 43. Creating Kafka Project

* fire up IntelliJ IDEA 
* create a new project
* choose maven (package manager)
* project SDK should be 1.8
* click next
* setup artifact coordinates
    * groupid: io.github.achliopa
    * artifactid: kafka-beginners-course
    * version: 1.0
* project name: kafka-beginners-course
* click finish
* in our project tree we see a file called 'pom.xml' it contains all code dependencies
* we need to add kafka as dependency (kafka and logger)
* we go to [maven kafka repo](https://mvnrepository.com/artifact/org.apache.kafka)
* select kafka clients and get the proper version depnding on the kafka version we use in our case [2.4.1](https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients/2.4.1)
* we cp the maven dep artifact in the pom.xml <dependencies> tag
* a pop up appears asking us to import changes. we select it.. if we miss it we can go to View=>Tool Windows => Maven and click the refresh button
* we need to download an slf4j logger (sl4fj simple). in maven site (where we gor kfka-client) we typw slf4j-simple and click on [last stable version](https://mvnrepository.com/artifact/org.slf4j/slf4j-simple/1.7.30) and cp the maven artifact in pom.xml <dependencies> do auto import and compent out scope test
* our total addition to pom.xml is the <dependencies> tag and whats inside it
```
    <version>1.0</version>
...................
    <dependencies>
        <!-- https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients -->
        <dependency>
            <groupId>org.apache.kafka</groupId>
            <artifactId>kafka-clients</artifactId>
            <version>2.4.1</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/org.slf4j/slf4j-simple -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>1.7.30</version>
<!--            <scope>test</scope>-->
        </dependency>

    </dependencies>
...................
</project>
```

* we ll do a hellow world app to test that all is ok and workinf
* in our projecttree => src => main => java RCLICK new=> package and name it com.github.achliopa.kafka
* RCLICK on it on prjecttree and new=>package com.github.achliopa.kafka.tutorial1
* in it we add a javaclass (RCLICK => new=>JavaClass) and name it ProducerDemo
* in the class at the source file use autocomplete to create a main method psvm+TAB
* add a println
```
package com.github.achliopa.kafka.tutorial1;

public class ProducerDemo {
    public static void main(String[] args) {
        System.out.println("hello world");
    }
}

```
* click play => run. we see hello world in console so all is OK

### Lecture 44. Java Producer

* we use our ProducerDemo class
* we remove the println from  the main
* first thing to create the Java Producer is to create producer properties
* then we create the producer
* then we send data
* we instantiate a new Properties object `Properties properties = new Properties();`
* with TAB it adds automatically the import `import java.util.Properties;`
* to see what we need to pass in the PRoperties we go to [kafka docs](https://kafka.apache.org/documentation/#producerconfigs)
* we pass them with settermethod using keyvalue pairs
* we need to pass bootstrap-servers `properties.setProperty("bootstrap.servers","127.0.0.1:9092");`
* then we need to pass key and value serializers. these help the producer know what kind of value we send to kafka and how it will be converted to bytes
* kafka offers ready serializers for us (use TAB to import them in the file)
```
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer",StringSerializer.class.getName());
```

* hardcoding property keys is oldschool we can use ProducerConfig.
```
 properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());
```

* to create a producer we use a Kafka Producer class `KafkaProducer <String,String> producer = new KafkaProducer<String, String>(properties);`
* <String,String> means that both key and value will be strings (therefore the serializers)
* to send data we will use `producer.send();` it takes a ProducerRecord as input. we create it beforehand
```
ProducerRecord<String,String> record = new ProducerRecord<String, String>("first_topic","hello world");
```

* we pass in the topic and the value (no key). IntelliJ also gives us hints in code so we know what are the strings we pass
* we can now send the record `producer.send(record);`
* we start a console consumer to see if it will work `kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic first_topic -- group my-second-group`
* run the code. we see nothing in consumer. why??? because  sending data is asynchronous
* before the send completes the program exits....
* to make sure data is send before we move on we use `producer.flush()` 
* if we want to flush and close the producer (if we dont have anything to do more) `producer.close()`
* our working code complete is
```
package com.github.achliopa.kafka.tutorial1;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class ProducerDemo {
    public static void main(String[] args) {
        // create Producer properties
        Properties properties = new Properties();
        String bootstrapServers = "127.0.0.1:9092";
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());
        // create Producer
        KafkaProducer<String,String> producer = new KafkaProducer<String, String>(properties);
        // create a ProducerRecord
        ProducerRecord<String,String> record = new ProducerRecord<String, String>("first_topic","hello world");
        // send data
        producer.send(record);
        //flush and close
        producer.close();
    }
}
```

### Lecture 45. Java Producer Callbacks

* cp the sourcefile ProducerDemo and renameit ProducerDemoWithCallback
* we will add a callback in send() to handle the async call
* so apart from ProducerRecord we pass in a callback we write newCallback hit TAB and autocompete takes effect 
* the event that will trigger it is onCompletion
* it executes everytime a record is successfully send or we have an exception
* we will use it to produce log based on recordMetadata (timestamp etc)
* for this we need a org.slf4j.Logger object
* we create a Logger for the class ` Logger logger = LoggerFactory.getLogger(ProducerDemoWithCallBack.class.getName());`
* we run the code and see the log in the console
* we put it in a for loop to send 10 messages
* our send with the callback for logging + for loop is
```
        for(int i=0;i<10;i++){
            // create a ProducerRecord
            ProducerRecord<String,String> record = new ProducerRecord<String, String>("first_topic","hello world" + Integer.toString(i));
            // send data - asynchronous
            producer.send(record, new Callback() {
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    // executes every time a record is successfully sent or an exception is thrown
                    if (e == null) {
                        // the record was successfully sent
                        logger.info("Received new metadata. \n"+
                                "Topic: " + recordMetadata.topic() + "\n" +
                                "Partition: " + recordMetadata.partition() + "\n" +
                                "Offset: " + recordMetadata.offset() + "\n" +
                                "Timestamp: " + recordMetadata.timestamp());
                    } else {
                        logger.error("Error while producing: " + e);
                    }
                }
            });
        }
```

* we have 3 partitions and use no keys, so we see that order is not kept as there is round robin but partition selection is arbitrary

### Lecture 46. Java Producer with Keys

* we duplicate the ProducerDemoWithCallBack as ProducerDemoKeys
* we will mod the ProducerRecord instantiation to add the key
```
            String topic = "first_topic";
            String value = "hello world " + Integer.toString(i);
            String key = "id_" + Integer.toString(i%3);
            ProducerRecord<String,String> record = new ProducerRecord<String, String>(topic,key,value);
            // send data - asynchronous
```

* we expect the  messages with same key to go to the same partitions
* to verify this behaviour we can do some things.
* we can make the call synchronous (BAD BAD PRACTICE)
* we chain .get() to send() and with Alt+ENTER we 'add exception to method signature'
* what we did is block send to make it synchronous. so in the for loop one after the other
* we run the code and see the log.. behaviour is confirmed
    * key id_0 goes to partition 1
    * key id_1 goes to partition 0
    * key id_2 goes to partition 2

### Lecture 46. Java Consumer

* we create a new java class ConsumerDemo
* we add a main and in it a Logger
* we add properties. the properties for the consumer we can find in [kafka docs](https://kafka.apache.org/documentation/#consumerconfigs)
* we use setters like in producer
```
  Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
```

* the last property is to reset the offset it accepts earliest,latest or none
    * earliest: recieve all messages in topic
    * latest: receive only messages after the offset (unread)
    * none: if offset not set throw error
* we need
    * create the consumer `KafkaConsumer<String,String> consumer = new KafkaConsumer<String, String>(properties);`
    * subscribe to the topic(s), `consumer.subscribe(Collections.singleton(topic));`
    * or to array of topics `consumer.subscribe(Arrays.asList(topic));`
    * poll for data
* we poll data in a while loop
* we add the poll method with a timeout (ALT+ENTER to fix dependencies)
* we also do log
```
while(true) {
            ConsumerRecords<String,String> records = consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String,String> record : records) {
                logger.info("Key: " + record.key() + ", Value: " + record.value());
                logger.info("Partitions: " + record.partition() + ", Offset: " + record.offset());
            }
        }
```

* we run the code. we see that consumer reads one partition after the other in order

### Lecture 48. Java Consumer inside Consumer Group

* we cp ConsumerDemo file to ConsumerDemoGroups
* if we run again the code as is we will not get any messgae from the topic.
* this is strange as we use Reset Offset earliest which is like --from-beginning
* if we do a kafka-consumer-groups.sh CL command with --describe for this group we see 0 lag
* if we want to read from beginneing we have to either reset group id or use a new one
* we will show rebalance. we run two consumers from code at the same time
* when we start the second consumer in the group we see in the log that the group is rebalancing
* and we see a new partition assignment among consumers that we can confirm in --describe
* to reassign consumers leave and rejoin the group

### Lecture 49. Java Consumer with Threads

* we want to get rid of the while loop
* we cp the code file to ConsumerDemoWithThreads
* we will use threads
* outside of main we add `public class ConsumerThread implements Runnable` which complains
* we need to add the default methods.. Alt+ENTER => implement Methods it adds run()
* we also add a constructor and a shutdown()
* in the constructor we pass a CountDownLatch object for concurrency
* we put the while true loop in run
* also we refctor the code moving propertis and KafkaConsumer in the contructor
* in shutdown we run the wakeup() method
```
 public void shutdown() {
            // the wakeup method is a special method to interrupt .poll()
            // it will throw the WakeupException
            consumer.wakeup();
        }
```

* then we wrap the while() loop in a try/catch to catch this esception. when we catch it we log and then
    * we `.close()` the consumer
    * use `latch.countdown()` to tell main thread hs finished
* we instantiate the runnable (thread) in main
* to pass the latch in we need to move out the code in a separate method and call it from main or make latch static what we dont want
* we create a thread from runnable and start it
* also we add a shutdown hook to shutdown the runnable and errorhadling code
* the complete code is
```
package com.github.achliopa.kafka.tutorial1;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class ConsumerDemoWithThreads {
    public static void main(String[] args) {
        new ConsumerDemoWithThreads().run();
    }

    private ConsumerDemoWithThreads() {

    }

    private void run(){
        Logger logger = LoggerFactory.getLogger(ConsumerDemoWithThreads.class.getName());
        String bootstrapServers = "127.0.0.1:9092";
        String groupId = "my-fifth-application";
        String topic = "first_topic";

        // a latch for dealing with multiple threads
        CountDownLatch latch = new CountDownLatch(1);

        // create the consumer runnable
        Runnable myConsumerRunnable = new ConsumerRunnable(
                topic,
                bootstrapServers,
                groupId,
                latch
        );

        // start the thread
        Thread myThread = new Thread(myConsumerRunnable);
        myThread.start();

        // add a shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            logger.info("Caught shutdown hook");
            ((ConsumerRunnable) myConsumerRunnable).shutdown();
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info("application has exited");
        }

        ));

        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.error("Application got interrupted",e);
        } finally {
            logger.info("Application is closing");
        }
    }

    public class ConsumerRunnable implements Runnable {

        private Logger logger = LoggerFactory.getLogger(ConsumerRunnable.class.getName());
        private CountDownLatch latch;
        private KafkaConsumer<String,String> consumer;

        public ConsumerRunnable(String topic, String bootstrapServers, String groupId, CountDownLatch latch){
            this.latch = latch;
            Properties properties = new Properties();
            properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
            consumer = new KafkaConsumer<String, String>(properties);
            consumer.subscribe(Arrays.asList(topic));
        }

        @Override
        public void run() {
           try {
               // poll for new data
               while(true) {
                   ConsumerRecords<String,String> records = consumer.poll(Duration.ofMillis(100));

                   for (ConsumerRecord<String,String> record : records) {
                       logger.info("Key: " + record.key() + ", Value: " + record.value());
                       logger.info("Partitions: " + record.partition() + ", Offset: " + record.offset());
                   }
               }
           } catch(WakeupException e) {
                logger.info("Received shutdown signal");
           } finally {
               consumer.close();
               // tell main code we are done with the consumer
               latch.countDown();
           }
        }

        public void shutdown() {
            // the wakeup method is a special method to interrupt .poll()
            // it will throw the WakeupException
            consumer.wakeup();
        }
    }
}
```

* we run the code. clear the console and hit exit

### Lecture 50. Java Consumer Seek and Assign

* we cp ConsumerDemo file as ConsumerDemoAssignSeek
* Assign and Seek is another way of writing an app
* we wont use the group id in the consumer
* we will also wont subscribe to topics
* assign and seek are mostly used to replay data or fetch a specific message
* in assign we pass a collection of topic partitions and in seek the offset
* so instead of subscribe to a topic we do
```
        //assign
        TopicPartition partitionToReadFrom = new TopicPartition(topic, 0);
        long offsetToReadFrom = 15L;
        consumer.assign(Arrays.asList(partitionToReadFrom));
        //seek
        consumer.seek(partitionToReadFrom,offsetToReadFrom);
```
* in the while loop we exit after reading 5 messages
* assign and seek reads for the topic and partition we spec after the offset in our case 5 messages

### Lecture 51. Client Bi-Directional Compatibility

* as of kafka 0.10.2 our clients & Kafka Bokers have a capability called bi-directional compatibility (because API calls are now versioned)
* an OLDER client (e.g v1.1) can talk to a NEWER Broker (e.g v2.0)
* an NEWER client (e.g v2.0) can talk to a OLDER Broker (e.g v1.1)
* Always use the latest client library version if we can [blog](https://www.confluent.io/blog/upgrading-apache-kafka-clients-just-got-easier/)

## Section 8: ===== Kafka Real World Project =====

### Lecture 53. Real World Project Overview

* We will build a Kafka Producer that will read from twitter and publish to Kafka
* Then we will write a consumer that reads from the Kafka topic and publishes to ElasticSearch

### Lecture 54. Real World Exercise

* Real-World Exercise: Before jumping to the next section for the solution, here are some pointers for some exercises:
* Twitter Producer: The Twitter Producer gets data from Twitter based on some keywords and put them in a Kafka topic of your choice
    * [Twitter Java Client](https://github.com/twitter/hbc)
    * [Twitter API Credentials](https://developer.twitter.com/)
* ElasticSearch Consumer: The ElasticSearch Consumer gets data from your twitter topic and inserts it into ElasticSearch
    * [ElasticSearch Java Client](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/6.4/java-rest-high.html)
    * [ElasticSearch setup](https://www.elastic.co/guide/en/elasticsearch/reference/current/setup.html) or [Alternatively](https://bonsai.io/)

## Section 9: Kafka Twitter Producer & Advanced Configurations

### Lecture 56. Twitter Setup

* we need a [twitter developer](https://developer.twitter.com/) account (check)
* we need to apply for it. it takes 2 weeks to get approved 
* Once accepted login
* click apps => create an app
* give it a unique name "twitter_kafka_rt_feed_achliopa" and a description
* give a url no need to put anything just a rationale (100 chars) and click Create
* go to keys and tokens => create and access token/access token secret (safegurad them)
* when we are done with the App we should regenerate API keys to block any unauthorized access
* go to [github twitter java](https://github.com/twitter/hbc) which is a Java HTTP Client to Twitter Streaming API named HoseBird Client
* in the Readme.md we see the Maven project dependeny. we cp it to pom.xml <dpendencies> in our project in IntelliJ
```
    <dependency>
      <groupId>com.twitter</groupId>
      <artifactId>hbc-core</artifactId> <!-- or hbc-twitter4j -->
      <version>2.2.0</version> <!-- or whatever the latest version is -->
    </dependency>
```

### Lecture 57. Producer Part 1 - Writing Twitter Client

* in our project tree we click on java and New=>package and name it 'com.github.achliopa.kafka.tutorial2'
* in it we create a new java class 'TwitterProducer'
* we create a main (psvm+TAB) and a system.out.println to test run
* in main we need to 
    * create a twitter client
    * create a kafka producer
    * loop to send tweets to kafka
* we refactor the class to a proper class with constructor run methods
* we call the run from main
* we add a method createTwitterClient()
* we follow the Quickstart instructions from HBC
    * setup the blocking queues
    * we opt to follow term "kafka"
    * we do all imports
    * we set up the keys
* we create the client again acording to docs
* the complete create Twitter client create method is
```
    public Client createTwitterClient() {
        /** Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream */
        BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(100000);

        /** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
        // Optional: set up some followings and track terms
        List<String> terms = Lists.newArrayList("kafka");
        hosebirdEndpoint.trackTerms(terms);

        // These secrets should be read from a config file
        Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, secret);

        ClientBuilder builder = new ClientBuilder()
                .name("Hosebird-Client-01")                              // optional: mainly for the logs
                .hosts(hosebirdHosts)
                .authentication(hosebirdAuth)
                .endpoint(hosebirdEndpoint)
                .processor(new StringDelimitedProcessor(msgQueue));

        Client hosebirdClient = builder.build();
        return hosebirdClient;
    }
```

* the client will put the messages to the message queue
* we refactor the method taking out the msgQueue and passing it as param
* this is to have avaiable in the run() for kafka
* we will just put a println to see if our code works outputting to the console
* we also need a logger to log the message
```
        // on a different thread, or multiple different threads....
        while (!client.isDone()) {
            String msg = null;
            try {
                msg = msgQueue.poll(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                client.stop();
            }
            if(msg != null) {
                logger.info(msg);
            }
```

### Lecture 58. Producer Part 2 - Writing the Kafka Producer

* we create the kafka producer like we did before
* we create it in a separate function `createKafkaProducer()`
```
    public KafkaProducer<String,String> createKafkaProducer(){
        String bootstrapServers = "127.0.0.1:9092";
        // create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());
        // create Producer
        KafkaProducer<String,String> producer = new KafkaProducer<String, String>(properties);
        return producer;
    }
```

* in the while loop after logging the msg we send it to kafka
```
if(msg != null) {
                logger.info(msg);
                producer.send(new ProducerRecord<>("twitter_tweets",null,msg), new Callback(){
                    @Override
                    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                        if(e != null){
                            logger.error("Something bad happened: "+e);
                        }
                    }
                });
            }
```

* we go and create the topic in kafka `kafka-topics.sh --bootstrap-server localhost:9092 --topic twitter_tweets --create --partitions 6 --replication-factor 1`
* we also fire up a console consumer to see the published tweets `kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic twitter_tweets`
* we run the app and it works
* we move out te terms in the class params together with the rest of the config
* we will tweet about 'kafka' on our account to see that we receive the tweet in our kafka consumer!. IT WORKS
* we will add a shutdown hook to exit gracefully when we shutdown our app from debugger
```
        //add a shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            logger.info("stopping application...");
            logger.info("shuting down client from twitter...");
            client.stop();
            logger.info("closing producer...");
            producer.close();
            logger.info("done!");
        })
```

### Lecture 59. Producer Configurations Introduction

* when producer is created the config values are passed in the log
* we ll see how to properly set them

### Lecture 60. acks & min.insync.replicas

* our app has acks=1
* if acks=0 (no ack) then:
    * no response is requested
    * if broker goes offline or an exception happens we wont know and lose data
    * its ok when we can afford to lose data (e.g metrics collection,log collection)
* if acks=1 (leader ack) then:
    * leader response is requested, but replication is not a guarantee (happens in background)
    * if an ack is not received then producer may retry
    * if the leader broker goes down before replica's replicate the data, we have data loss and leader ISRs are not synchronized
* if acks=all (replicas ack) then:
    * Leader+Replicas ack requested
    * after leader gets acknoledgement from replicas for data replication it acknowledges to producer
    * it adds latency and safety
    * it guarantees no data loss if replicas are enough
    * its a necessary if we dont want to lose data
* acks=all MUSt be used in conjunction to `min.insync.replicas`
* `min.insync.replicas` can be set at the broker or topic level (override)
* `min.insync.replicas=2` implies that at least 2 brokers that are ISR (including leader) must respond that they have the data
* that means tha if we use `replication.factor-3`,`min.insync.replicas=2`,`acks=all` we can only tolerate 1 broker going down, otherwise the producer will receive an exception (NOT_ENOGH_REPLICAS) on send

### Lecture 61. retries, delivery.timeout.ms & max.in.flight.requests.per.connection

* in case of transient failures, developers are expected to handle exceptions, otherwise the data will be lost
* Example of transient failure
    * NotEnoughReplicasException
* There is a "retries" setting
    * default is 0 for Kafka <= 2.0>
    * defaults to 2147483647 times for Kafka >= 2.1
* the `retry.backoff.ms`  setting is by default to 100ms (frequency of retry)
* if retries > 0 for example retries = 2147483647
* the producer wont try the request for ever, its bounded by a timeout
* we can set an intuitive Producer Timeout (KIP-91 - Kafka 2.1)
    * `delivery.timeout.ms` = 120000ms == 2min
* Records will be failed if they can't be acknowledged in delivery.timeout.ms
* WARNING: in case of retries, there is a chance that messages will be sent out of order (if a batch has failed to be sent)
* if we rely on key based ordering this can be a big issue
* for this we can set the setting while controls how many produce requests can be made in parallel `max.in.flight.requests.per.connection`
    * Default: 5
    * Set it to 1 if we want to ensure ordering (this may impact throughput)
* If we use Kafka >=1.0.0 there is a better solution: Idempotent Producers

### Lecture 62. Idempotent Producer

* The problem: the Producer can introduce duplicate messages in Kafka due to network errors
* Kafka sends the ack but Producer never gets it due to network error
* pRoducer retries and there is a produce duplicate
* With Idempotent Producer (Kafka >=0.11):
    * it wont introduce duplicates on network error
    * when producer retries it sends a request produce id. kafka broker sees its a duplicate request and does not commit it but sends an ack
* Idempotent producers are great to guarantee a stable and safe pipeline
* They come with:
    * retries = Integer.MAX_VALUE(2^31-1 = 2147483647)
    * `max.in.flight.requests`=1 (Kafka ==0.11) or
    * `max.in.flight.requests`=5 (Kafka >= 1.0 -higher performance and keep ordering)
    * `acks=all`
* These settings are applied automatically after our producer has started if we don't set them manually
* To use them set `producerProps.put("enable.idempotence",true)`

### Lecture 63. Producer Part 3 - Safe Producer

* if Kafka < 0.11
    * `acks=all` (producer level) ensures data is properly replicated before an ack is received
    * `min.insync.replicas=2` (broker/topic level) ensures two brokers in ISR at least have the data after an ack
    * `retries=MAX_INT` (producer level) ensures transient errors are retried indefinitely
    * `max.in.flight.requests.per.connection=1` (producer level) ensures only one request is tried at any time. preventing message re-ordering in case of retries (otherwise set to 5)
* if Kafka >= 0.11
    * `enable.idempotence=true` (producer level) + `min.insync.replicas=2` (broker/topic level)
    * it implies `acks=all`,`retries=MAX_INT`,`max.in.flight.requests.per.connection=1` if Kafka 0.11 or 5 if Kafka >= 1.0
    * while keeping ordering guarantees and improving performance
* Running a "safe producer" might impact throughput and latency always test for your use case
* go back to our running code log and see the config dump and all the settings
```
// create a safe producer
        properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,"true");
        properties.setProperty(ProducerConfig.ACKS_CONFIG,"all");
        properties.setProperty(ProducerConfig.RETRIES_CONFIG,Integer.toString(Integer.MAX_VALUE));
        properties.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,"5"); // kafka 2.0 >= 1.1 so we can keep this as 5, use 1 otherwise
```

* only the first prop s required. the rest is added explicitly for clarity
* run again and check the log for config changes

### Lecture 64. Producer Compression

* Producer usually send data that is text-based, for example with JSON-data
* in that case its paramount to apply compression to the producer
* compression is enabled at the Producer level and doesn't require any configuration change in the Brokers or in the Consumers
* `compression.type` can be `none` (default),`gzip`,`lz4`,`snappy`
* compression is more effective the bigger the batch of message being sent to Kafka!
* Benchmarks [here](https://blog.cloudflare.com/squeezing-the-firehose/)
* a Producer sends a batch of messages if possible to save resources
* before he sends the batch he compresses it
* the compressed batch has following advantages
    * much smaller producer request size (compression ration up to 4x)
    * faster to transfer data over the network => less latency
    * better throughput
    * better disk utilization in Kafka (stored messages on disk are smaller)
* disadvantages:
    * producer must commit some CPU cycles to compression
* overall:
    * consider testing snappy or lz4 for optimal speeed/ compression ratio
    * gzip has best compression but slower
* find a compression algorithm that gives you the best performance for your specific data. test all of them
* always use compression in prod, especially in high throughput
* consider tweaking Producer Batching with `linger.ms` and `batch.size` to have bigger batches and therefore more compression and higher throughput

### Lecture 65. Producer Batching

* by default Kafka tries to send records as soon as possible
    * it will have up to 5 requests in flight, meaning up to 5 messages individually sent at the same time
    * after this, if more messages have to be sent while others are in flight, Kafka is smart and will start batching them while they wait to send them all at once
* this smart batching allows Kafka to increase throughput while maintaining very low latency
* batches have higher compression ratio so better efficiency
* so how can we control the batching mechanism
* `linger.ms` number of milliseconds a producer is willing to wait before sending a batch out (default 0)
* by introducing some lag (for example `linger.ms=5`), we increase the chances of messages being sent together in a batch
* at the expense of small delay, we can increase throughput, compression and efficiency of our producer
* the max batch size allowed in a batch is `batch.size`. default is 16KB
* increasing it to 32KB or even 64KB can help increae compression etc etc
* any message bigger than the max batch size wont be batched
* a batch is allocated per partition, so make sure that you dont set it to a number that is too high, otherwise we ll run waste memory
* we can monitor the average batch size metric using Kafka Producer Metrics

### Lecture 66. Producer Part 4 - High Throughput Producer

* we'll add snappy message compression in our producer
* snappy is very good if our messages are text based, e.g log lines or JSON docs
* snappy is a good balance of CPU/compression ratio
* we ll also increase `batch.size` to 32KB and introduce a small delay of 20ms `linger.ms`
* its again a set of props setting
```
        // high throughput producer (at the expense of CPU usage and latency
        properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG,"snappy");
        properties.setProperty(ProducerConfig.LINGER_MS_CONFIG,"20");
        properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG,Integer.toString(32*1024));
```
* we change  term to "covid19" which is HOT these days as we are in a Pandemic Quarantine and twitter is on fire with this as watch
* we see that consumer decompresses the messages and we see them as before and its FASTT

### Lecture 67. Producer Default Partitions and Key Hashing

* How keys are hashed
* by default, our keys are hashed using the ["murmur2"](https://en.wikipedia.org/wiki/MurmurHash) algorithm
* it is most likely preferred to not override the behavior of the partitioner, but it is possible to do so (`partitioner.class`)
* target formula with the default partitioner `targetPartition = Utils.abs(Utils.murmur2(record.key()))%numpartitions;`
* if we aposteriory add partitions to a topic it will SCREW the formula. DONT DO IT!!!

### Lecture 68. [Advanced] max.block.ms and buffer.memory

* if the producer produces faster than the broker can take, records will be buffered in memory
* `buffer.memory`=33554452 (32MB) by default: the size of the send buffer at producer
* that buffer will fill up over time and fill back down when the throughput to the broker increases
* if buffer is full (all 32MB) then the send() method will start to block (wont return right away)
* `max.block.ms`=60000: the time the .send() will block untill throwing an exception. Exception are basically thrown when:
    * the producer has filled up its buffer
    * the broker is not accepting any new data
    * 60 seconds has elapsed
* If we hit an exception hit that usually means our brokers are down or overloaded as they can't respond to requests

### Lecture 69. Refactoring the Project

* RCLICK to project(kafka-beginners-course) => New => Module (name=kafka-basics)
* cp our package 'com.github.achliopa.kafka' and paste it in kafka-basics->src->main->java
* delete tutorial2 from the new module
* RCLICK to project(kafka-beginners-course) => New => Module (name=kafka-producer-twitter)
* cp our package 'com.github.achliopa.kafka' and paste it in kafka-producer-twitter->src->main->java
* delete tutorial1 from the new module
* delete package 'com.github.achliopa.kafka'
* modify pom.xml
* cut the <dependencies> from the original package and paste it to the 2 new kafka-basics and kafka-producer-twitter
* in kaka-basics we dont need twitter-client dependency so remove it
* this is a decoupled project ready for the next step

## Section 10: Kafka ElasticSearch Consumer & Advanced Configurations

### Lecture 71. Setting up ElasticSearch in the Cloud

* 