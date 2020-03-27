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

* we can run elasticsearch locally or in AWS or use a hosted service like [bonsai](https://bonsai.io/)
* we get a free 3node cluster 
* it does not work so we will run locally for the project
* we start elasticsearch and kibana locally

### Lecture 72. ElasticSearch 101

* we can use postman to fire up elasticsearch queries against our local installation or Kibana console
* we send `GET /_cat/health?v` to see cluster health
* we send `GET /_cat/nodes?v` to see node info
* we send `GET /_cat/indices?v` to list indices
* we create an index `PUT /twitter` with name twitter
* we put a doc in our index named tweets with index 1 and JSON format
```
PUT twitter/tweets/1
{
  "course":"Kafka for Beginners",
  "instructor":"Stephan",
  "module":"elasticsearch"
}
```

* we can get the tweet `GET /twitter/tweets/1`

### Lecture 73. Consumer Part 1 - Setup Project

* to connect to elasticsearch we need to add a maven dependency for RESTful API. we get it from [elastic.co](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-getting-started-maven.html)
```
<dependency>
    <groupId>org.elasticsearch.client</groupId>
    <artifactId>elasticsearch-rest-high-level-client</artifactId>
    <version>7.6.1</version>
</dependency>
```
* in IntelliJ we create a new=>module 'kafka-consumer-elasticsearch'
* in its pom.xml file we add <dpendencies></dependencies> and put the elasticsearch restAPI dependency above
* from the other projects in IDE pom.xml files we cp the kafka and logger depnedecies
* we add a new package 'com.github.achliopa.kafka.tutorial3' in src=>main=>java of the module and add in it a new java class 'ElasticSearchConsumer'
* we add a method to create the ES client
```
    public static RestHighLevelClient createClient() {
        String hostname = "";
        String username = "";
        String password = "";

        // dont do it if you run a local ES
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username,password));

        RestClientBuilder builder = RestClient.builder(
                new HttpHost(hostname, 9200, "http" ))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                });

        RestHighLevelClient client = new RestHighLevelClient(builder);
        return client;
    }
```

* in main() we create the client `RestHighLevelClient client = createClient();`
* we use an IndexRequest to get an Index. we need index,type and id. also some Json content if its needed
```
        String jsonString = "{ \"foo\": \"bar\"}";

        IndexRequest indexRequest = new IndexRequest(
                "twitter",
                "tweets"
        ).source(jsonString, XContentType.JSON);
```

* this will fail if index does not exist
* we run the request with
```
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        String id = indexResponse.getId();
        logger.info(id);
```

* when we finish we must `client.close();`
* we run the code. IT WORKS! id is 0A_qFXEBRsfJzUu4zomC
* we confirm by cp the id returned in Kibana console issuing a `GET /twitter/tweets/0A_qFXEBRsfJzUu4zomC`

### Lecture 74. Consumer Part 2 - Write the Consumer & Send to ElasticSearch

* we want all data from our tweets topic in ES
* we need a consumer.. we will use the ConsumerDemoGoups we have ready
* we cp the setup code in a new static method
```
public static KafkaConsumer<String,String> createConsumer(){
        String bootstrapServers = "127.0.0.1:9092";
        String groupId = "kafka-demo-elasticsearch";
        String topic = "twitter_tweets";
        // create consumer configs
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        // create consumer
        KafkaConsumer<String,String> consumer = new KafkaConsumer<String, String>(properties);
        return consumer;
    }
```

* we add the while loop from ConsumerDemoGroups into main in the for loop we will insert data into ES
* we will insert `record.value()`
* the while loop
```
// poll for new data
        while(true) {
            ConsumerRecords<String,String> records = consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String,String> record : records) {
                // here we insert data into ES
                String jsonString = record.value();

                IndexRequest indexRequest = new IndexRequest(
                        "twitter",
                        "tweets"
                ).source(jsonString, XContentType.JSON);

                IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
                String id = indexResponse.getId();
                logger.info(id);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
```

* we run and it works

### Lecture 75. Delivery Semantics for Consumers

* At most once: offsets are committed as soon as the message batch is received. If the processing goes wrong, the message will be lost (it wont be read again)
    * read batch
    * committed offset
    * process data (e.g send email)
    * reads start from commit after restart
* at least once: offsets are committed after the message is processed, if the processing goes wrong, the message will be read again. This can result in duplicate processing of messages. make sure the processing is "idempotent" (processing again the messages wont impact the system)
    * reads batch
    * process data (e.g upsert in DB)
    * committed offset
    * reads start from commit after restart
* exactly once: only for Kafka => Kafka workflows using Kafka Streams API. For Kafka => Sink workflowsm use an idempotent consumer
* Bottom like: for most applications we should use at least oce processing (we'll see in practice how to do it) and ensure your transformations / processing are idempotent

### Lecture 76. Consumer Part 3 - Idempotence

* we use at least one in our app. but in our for loop we dont care about duplicates.
* so if something goes wrong in inserting to elasticsearch we will insert duplicates to ES
* to make it idempotent we need to take control of indexing in ES as indexes are Unique
* its simple as passing an id in the indexrequest bounded to the consumer offset `String id = record.topic() + "_" +record.partition() + "_" + record.offset();`
* this is a nice generic approach
* or we can have a twitter feed unique id
* we will use the google java json lib [gson](https://mvnrepository.com/artifact/com.google.code.gson/gson/2.8.5) we insert the dependency to the pom.xml
* we do it in a separate function
```
    private static JsonParser jsonParser = new JsonParser();

    private static String extractIdFromTweet(String tweetJson){
        // use gson lib
        return jsonParser.parse(tweetJson)
            .getAsJsonObject()
            .get("id_str")
            .getAsString();
    }
```
* we run the code and confirm idempotence

### Lecture 77. Consumer Poll Behaviour

* Kafka Consumers have a "poll" model while many other messaging bus in enterprises have a "push" model
* this allows consumers to control where in the log they want to consume and gives them ability to replay events
* consumer polls for data and broker replies ASAP with data or empty after timeout
* the polling behavious is controlled by:
* `fetch.min.bytes` default is 1: 
    * controls how much data we want to pull at least on each request
    * helps improving throughput and decreasing request number
    * at the cost of latency
* `max.poll.records` default  500: 
    * control how many records to receive per poll request (MAX)
    * we can increase this if we have small messages and a lot of available RAM
    * good to monitor how many records are polled per request and adjust
* `Max.partitions.fetch.bytes` default is 1MB:
    * max data returned by the broker per partition
    * if we read from 100 partitions, we ll need hefty RAM (calc)
* `Fetch.max.bytes` default 50MB:
    * max data returned for each fetch request (covers all partitions)
    * used when consumer does many fetches in parallel

### Lecture 78. Consumer Offset Commit Strategies

* there are two most common patterns to commiting offsets in a consumer app
* (easy) `enable.auto.commit =  true` + synchronous processing of batches:
    * with auto commit offsets will be committed automaticaly for us at regulat interval (`auto.commit.interval.ms` is 5000 by default) every time we call poll
    * if we dont use synchronous codem we will be in at-most-once behaviour because offsets will be commited before our data is processed
    * not recommended for beginners
```
while(true){
    List<Records> batch = consumer.poll(Duration.ofMillis(100))
    doSomethingSynchronus(batch)
}
```
* (medium) `enable.auto.commit = false` + manual commit of offsets:
    * we control when we commit offsets and whats the condition for commiting them
    * example: accumulate records into a buffer and then flush the buffer to a DB + commit offsets them
```
while(true){
    batch += consumer.poll(Duration.ofMillis(100))
    if isReady(batch){
        doSomethingSynchronous(batch)
        consumer.commitSync();
    }
}
```

### Lecture 79. Consumer Part 4 - Manual Commit of Offsets

* we will control the consumer offsets with a prop set `properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,"false"); //disable auto ofset commit`
* we will also set max records in a batch `properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,"10");`
* afer we loop in the records received in the batch puting them in ES we commit the offsets using `consumer.commitSync();`
* FIX: make sure to increase the fileds in index to avoid errors 
```
PUT twitter/_settings
{
  "index.mapping.total_fields.limit": 10000
}
```

### Lecture 80. Consumer Part 5 - Performance Improvement using Batching

* with current implementation we do one request per record in ES
* we dont batch
* we need a bulk request to bakend
* before the loop of index requests we do a bulk request `BulkRequest bulkRequest = new BulkRequest();`
* after we create tje indexrequests we dont send them but add them to bulk `bulkRequest.add(indexRequest);`
* after the for loop we send the bulk request to ES `BulkResponse bulkResponse = client.bulk(bulkRequest,RequestOptions.DEFAULT);`
* we increase batch to 100 in props to push it and get throughput
* we fix a bug to commit records to ES only when we get records from Kafka

### Lecture 81. Consumer Offsets Reset Behaviour

* a consumer is expected to read from a log continuously
* if consumer app has a bug it can go down
* kafka has retention of 7 days . if consumer is down for >7 days offsets are invalid
* consumer has  then to use:
    * `auto.offset.reset=true` so it will read from the end of the log
    * `auto.offset.reset=earliest` will read from the start of the log
    * `auto.offset.reset=none` will throw exception if no offset is found
* additionaly consumer offsets can be lost:
    * if a consumer hasnt read new data in 1 day (kafka <2.0 )
    * if a consumer hasnt read new data in 7 days (kafka >=2.0 )
* this is controlled by the broker setting `offset.retention.minutes`
* Replay data for consumers
* to replay data for a consumer group
    * take all consumers from a group down
    * use kafka-consumer-groups to set offset to what we want
    * restart consumers
* Conclusion:
    * set proper data retention period & offset retention period
    * ensure the auto offset reset behaviour is the one we want
    * use replay capablity in case of unexpected behaviour

### Lecture 82. Consumer Part 6 - Replaying Data

* to replay data we stop consumer app
* then `kafka-consumer.groups.sh --bootstrap-server localhost:9092 --grop kafka-demo-elasticsearch --reset-offsets --topic twitter_tweets --execute --to-earliest`
* this resets the consumer offset to start

### Lecture 83. Consumer Internal Threads

* say we have a consumer group with 3 consumers
* each consumer starts a poll thread to the broker and a heartbeat thread to the ConsumerCoordinator acting Broker 
* the heartbeat is control th poll is data
* heartbeat enables to detect consumers that are down and does rebalance
* to avoid issues consumers are encouradged to process data fast and poll often
* Consumer Heartbeat Thread
    * `session.timeout.ms` default 10sec
    * heartbeats are sent periodicaly to broker
    * if no heartbeat is sent during this periof consumer is considered dead
    * set to lower for fast consumer rebalance
    * `heartbeat.interval.ms` default 3sec
    * how often to send heartbeats
    * usually set to 1/3 of the `session.timeout.ms`
* Consumer Poll Thread
    * `max.poll.interval.ms` default 5min
    * maximum amount of time between two .poll() calls before declaring the consumer dead
    * this is particularly relevant for Big Data frameworks like Spark in case the processing takes time

## Section 12: Kafka Extended APIs for Developers

### Lecture 85. Kafka Connect Introduction

* Kafka-Connect is all about code & connectors re-use
* Why Kafka Connect and Streams?
* Four common kafka use cases:
    * Source => Kafka (ProducerAPI) : Kafka Connect Source
    * Kafka => Kafka (Consumer,Producer API) : Kafka Streams
    * Kafka => Sink (Consumer API) : Kafka Connect Sink
    * KAfka => A00 (Consumer API)
* Simplify and Improve getting data in and out of kafka
* simplify transforming data within Kafka without relying on external libs
* Why Kafka Connect?
    * Programmers always want to import data from the same sources: DBs,JDBC,SAP HANA,BlockChain,Cassandra,MongoDB,Titter,IOT,FTP
    * Programmers always want to store data in the same sinks: S3,ElasticSearch,HDFS,JDBC,SAP HANA,Cassandra,DynamoDB,HBase,Redis,mongoDB,
* Don't reinvent the wheel
* Connect Cluster stands between Sources and Kafka Cluster or between Kafka Cluster  and Sinks
* Connect Cluster has Workers (much like Brokers)
* Stream Apps input/output data to the Kafka Cluster 
* Kafka COnnect high Level
    * Source COnnectors to get data from Common Data Sources
    * Sink Connectors to publish that data in Common Data Stores
    * make it easy for non expert devs to quickly get their data reliable to Kafka
    * Part of the ETL pipeline
    * Scaling made easy from small pipelines to company-wide pipelines
    * Re-usable code
* [Connectors](https://www.confluent.io/hub/)

### Lecture 86. Kafka Connect Twitter Hands-On

* [Listo of Connectors](https://www.confluent.io/product/connectors-repository/) 
* Confluent connectos are Oficial
* Certidfied Connectors are Production Ready
* Community Connectors are more experimental
* we will use [Twitter Source Connector](https://github.com/jcustenborder/kafka-connect-twitter)
* we see readme.md for instructions and config options
* go to releases and get the last file. 
* we want it portable so we dont get the .deb file but the zip with the jar files
* we move it to IntelliJ workspace
    * into top project add directory kafka-connect
    * in it add folder connect
    * in it folder kafka-connect-twitter
    * drop there the extracted jars
* to run the connector we use `connect-standalone.sh ` and we need to edit the connect-standalone.properties in config file. we cp it to the project and edit it adding `plugin.path=connectors`
* we create in the project a twitter.properties file nd cp the props from github repo readme file
* we pass in the credentials and some params
```
process.deletes=false
filter.keywords=kafka
kafka.status.topic=twitter_status_connect
kafka.delete.topic=twitter_deletes_connect
```

* we create the 2 topics with
```
kafka-topics.sh --bootstrap-server localhost:9092 --topic twitter_status_connect --create --partitions 3 --replication-factor 1
kafka-topics.sh --bootstrap-server localhost:9092 --topic twitter_delete_connect --create --partitions 3 --replication-factor 1
```
* we run a consumer on status topic `kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic twitter_status_connect --from-beginning`

### Lecture 87. Kafka Streams Introduction

* Say we want to do the following from the twitter_tweets topic
    * filter only tweets that have over 10 likes or replies
    * Count the number of tweets received for each hashtag every 1 min
    * or combine both to get trending topics and hashtags in real time
* With Kafka Producer and Consumer we can achieve that but it very low level and not easy
* Kafka Streams
    * Easy Data processing and transformation library within kafka
    * Data transformation
    * Data enrichment
    * fraud detection
    * monitoring and alert
    * standard java app
    * no need for separate cluster
    * highly scalable, elastic and fault tolerant
    * exactly once capabilities
    * once record at a time processing (no batching)
    * works for any application size
    * they can take data from one or multiple topics and put it back to one or multiple topics
* Serious Contender to Apache Spark / Flink or NiFi
* Example: Tweets Filtering
    * we want to filter tweets topic and put the results back to Kafka
    * we basically want to chain a consumer with a producer
    * Tweets Topic => Consumer => Application Logic => producer => Filtered Topic
    * this is complicated and error prone especially if we deal with concurrency and error scenarios

### Lecture 88. Kafka Streams Hands-On

* we create a new module in our IntelliJ project called 'kafka-streams-filter-tweets'
* in its pom.xml file we add <dependencies></<dependencies>
* we go to [maven kafka streams](https://mvnrepository.com/artifact/org.apache.kafka/kafka-streams/2.4.1) and get dependency for our used kafka version
* cp it in pom.xml
* we cp slf4j dependency for logs from another project
* we create a new package in src->main->java named 'com.github.achliopa.kafka.tutorial4'
* in it we add a class StreamFilterTweets
* we add a main. in it the steps we l take are:
    * create properties
    * create a topology
    * build the topology
    * start our streams application
* we also add dependency for gson to parse JSON from tweet in the stream filter (take it from elasticsearch project)
* also we cp the extract string from JSON method alter it to return followers count
```
private static Integer extractUserFollowersFromTweet(String tweetJson){
        // use gson lib
        try {
            return jsonParser.parse(tweetJson)
                    .getAsJsonObject()
                    .get("user")
                    .getAsJsonObject()
                    .get("followers_count")
                    .getAsInt();
        } catch (NullPointerException e) {
            return 0;
        }
    }
```
* the main method complete is
```
    public static void main(String[] args) {
        //create properties
        Properties properties = new Properties();
        properties.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"127.0.0.1:9092");
        properties.setProperty(StreamsConfig.APPLICATION_ID_CONFIG,"demo-kafka-streams");
        properties.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
        properties.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
        //create a topology
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        //input topic
        KStream<String,String> inputTopic = streamsBuilder.stream("twitter_tweets");
        KStream<String,String> filteredStream = inputTopic.filter(
                // filter for tweets which has a user with over 10000 followers
                (k,jsonTweet)-> extractUserFollowersFromTweet(jsonTweet) > 10000
        );
        filteredStream.to("important_tweets");
        //build the topology
        KafkaStreams kafkaStreams = new KafkaStreams(
                streamsBuilder.build(),
                properties
        );
        //start our streams application
        kafkaStreams.start();
    }
```

* we now have to create the topic `kafka-topics.sh --bootstrap-server localhost:9092 --topic important_tweets --create --partitions 3 --replication-factor 1`
* we run the code also start our twitter producer and a console-consumer on important_tweets topic
* It F@@@@ WORKS!!!!

### Lecture 89. Kafka Schema Registry Introduction

* kafka takes bytes as an input and publishes them (that why we need serializer/deserializer)
* no data verifications
* Τhe Need  for a schema registry
    * what if the producer sends bad data
    * what if a field gets renamed?
    * what if the data format changes from one day to another?
    * Consumers Break!!!
* We need data to be self describable
* We need to be able to evolve data without breaking downstream consumers
* What if Kafka Brokers verified the messages they receive?
    * that would break kafka efficiency
    * kafka is just pass through
* Kafka Schema Registry: 
    * has to be a separate component
    * Producers and Consumers need to be able to talk to it
    * it must be able to reject data
* a common data format must be agreed upon
    * it needs to support schemas
    * it needs to support evolution
    * it needs to be lightweight
* Confluent Schema Registry!! used Apache Avro as data format
* Pipeline without Schema Registry: Source => Java Producer => Kafka =>java Consumer => Target System
* Confulet Schema Registry Purpose
    * Store and retrieve schemas for Producers/Consumers
    * Enforce Backward/Forward compatibility on topics
    * Decrease the size of payload
* Producer sends Avro Content to Kafka
* Producer sends Schema to Schema Registry
* Consumer reads Avro Content from Kafka
* Consumer gets Schema from Schema Registry
* utilizing a schema has a lot of benefits
* but it implies that we need to 
    * set it up well
    * make sure its higly available
    * Partially change the producer and consumer code
* Apache Avro  as a format is awesome but has a learning curve
* Schema Registry is free but it takes time to setup
* A talk about [Kafka APIs](https://medium.com/@stephane.maarek/the-kafka-api-battle-producer-vs-consumer-vs-kafka-connect-vs-kafka-streams-vs-ksql-ef584274c1e)

## Section 13: Real World Insights and Case Studies (Big Data / Fast Data)

### Lecture 91. Choosing Partition Count & Replication Factor

* these are the most important params when creating atopic
* they impact performance and durability of the system overall
* its paramount to get the params right from first time
    * if we change partition count after creation we break key ordering guarantee 
    * if the replication factor increases during a topic lifecycle, we put more pressure on our cluster, which can lead to unexpected performance decrease (brokers should do more work)
* Guess and TEST!!!!!
* each partition can handle a throughput of a few MB/s (measure it for the setup)
* more partitions: 
    * better parallelism,better throughput, 
    * ability to run more consumers in a group to scale
    * ability to leverage more brokers if we end up with a large cluster
    * BUT more elections to perform in Zookeeper
    * BUT more files open in Kafka
* Guidelines:
    * partitions/topic = $$$$$ Question
    * (intution) Small Cluster (<6 brokers): 2x #brokers
    * (intuition) Big Cluster (>12 brokers): 1x #brokers
    * adjust for #consumers we need to run in paralel at peak throughput (1 partition/consumer)
    * adjust for producer throughput (increase if super-high throughput or projected increase in next 2 years) doe 3x #brokers
    * replication factor should be at least 2 usually 3 at most 4
* the higher the replication factor (N)
    * better fault tolerance (N-1 brokers can fail)
    * BUT more replication => higher latency if acks=all
    * BUT more disk space on our system (50% more if RF is 3 than 2)
* Guideline
    * set it to 3 to get started (we need 3 brokers for it)
    * if replication perforace is an issue , get a better machine instead of less RF
    * NEVER SET IT TO 1 IN PRODUCTION
* Cluster Guidelines
    * it is pretty much accepted that a broker should not hold more than 2000 to 4000 partitions (across all topics)
    * additionally a kafka cluster should have max 20000 partitions across all brokers
    * why? when broker goes down zookeper stresses to perform leader elections
    * if we need more partitions on the cluster. add brokers
    * if we need more than 20000partitions?? do it like netflix and create more Kafka clusters

### Lecture 92. Kafka Topics Naming Convention

* Topic can have any names you want. It is very important to choose a naming convention in your company to maintain some kind of consistency.
* read [this](https://riccomini.name/how-paint-bike-shed-kafka-topic-naming-conventions) for ideas

### Lecture 93. Case Study - MovieFlix

* Netflix Alter Ego
* Reqs:
    * users to resume video where they left off
    * build user profile in no time
    * recommend next show in RT
    * store all data in analytics store
* Kafka Cluster:
* Topics:
    * show_position: VideoPlayer (wihile playing) => Video Position Service (Producer) => show_posiiton (topic) => Resume Service (Consumer) => VideoPlayer (while starting)
    * recommendations: show_posiiton => RecommendationsRTEngine(KafkaStreams) => recommendations => Recommendations Service (Consumer) => MoviesTVShowsPortal/Website
    * show_position,recommendations => Analytics Consumer (KafkaConnect) => Analytics Store (Hadoop)
* show_posiiton topic:
    * can have multiple producers (massive)
    * highly distributed if hogh_volume >30 partitions
    * key selection, "user_id"
* recommendation topic:
    * kafka streams recommendation engine may source data from analytical store for historical training
    * maybe low volume topic
    * key, "user_id"

### Lecture 94. Case Study - GetTaxi

* UBER clone
* Reqs:
    * user should match with closeby driver
    * prices should surge if number of drivers is low or num of users high
    * store accurate posiition data in analyticsto calculate cost
    * use kafka
* Topics
    * user_positioon: UserApp => UserPositionService(Producer) => user_position
    * taxi_position:: TaxiDriverApp => TaxiPositionService(Producer) => taxi_position
    * surge_pricing: user_positioon,taxi_position => SurgePricingComputationmodel(KafkaStreams)=> surge_pricing => TaxiCostService(Consumer)=>UserApp
    * surge_pricing => Analytics consumer (kafka connect) => Analytics Store (Amazon S3)
* user_positioon,taxi_position
    * multiple producers
    * highky distributed,high volume (>30partitions)
    * keys "taxi_id","user_id"
    * data epehemeral dont keep them long in kafka 
* surge_pricing:
    * computation of surge pricing comes from kafka streams
    * surge pricing may be regional and therefore high volume
    * other topics like "events" or "weather" might used from kafka streams app

### Lecture 95. Case Study - MySocialMedia

* CQRS (CommandQueryRequestSegregation) App - Instagram Clone
* Reqs
    * user to post,like and comment
    * user to see total num of likes comments per post in RT
    * high volume from day 1
    * users should be able to see 'trending' posts
    * kafka used
* Topics
    * posts: UserApp(userposts)=>postingservice(Producer) => posts
    * likes: UserApp(userlikes)=>Like/Comment Service(Producer) => likes
    * comments: UserApp(userComments)=>Like/Comment Service(Producer) => comments
    * posts_with_counts: posts,likes,comments => Total Likes/Comments/Computation (Kafka Streams)=>posts_with_counts=>refresh feed service(consumer)=>website
    * trending_posts:  posts,likes,comments => Trendingposts in last hour (Kafka Streams)=>trending posts => trending feed service (kafkaconnect)=>website
* Responsibilities are segregated  so we call model CQRS
* Posts: multiple produces,high volume highly distributed >30paritions, "user_id" key, high retention
* Like/Comments: multiple produces,high volume highly distributed >100paritions, "post_id" key, high retention
* Data in kafka should be formatted as events: user_123 created post_456 at 2pm

### Lecture 96. Case Study - MyBank

* digital bank
* Reqs:
    * alert user in case of large tranactions,fraud
    * transction data already in db
    * thresholds defined by users
    * alerts in RT to users
* Topics:
    * bank_transactions: DB => Kafka Connect Source (CDC Connector - [Debezium](https://debezium.io/))=> bank_transactions
    * user_settings: UserApp(thresholdsetting)=>AppThresholdService=>user_settings
    * user_alerts: bank_transactions,user_settings=>RT big transactions detection(kafka_streams)=>user_alerts=>notification service(consumer)=>NotificationService
* bank_transactions topic:
    * kafka connect source is a great way to expose data from existing DB
    * Tons of CDC available (shange data capture) for all DBs
* Kafka Streams App:
    * when user changes settigns alerts wont gen for past transactions
* User_thresholds topics: its better to sent events to the topic: user 123 enabled threshold at $1200 at 12pm 12/12/23 than sending state to the user

### Lecture 97. Case Study - Big Data Ingestion

* it is common to have generic

### Lecture 98. Case Study - Logging and Metrics Aggregation

* it is common to have "generic" connectors or solutions to offload data from Kafka to HDFS,Amazon S3, ES for example
* it is common to hve Kafka serve a "speed layer" for RT applications, while having a "slow layer" which helps with data ingestions into stores for later analytics
* Kafka as a front to BigData Ingestion is a common pattern in BigData to provide an "ingestion buffer" in front of some stores
* BigData Ingestion Architecture:
    * Data Producers: App,Website,FinTech Systems,Email,DBs => Kafka
    * Kafka => Spark/Storm/Flink => RT Analytics/Dashboards/Apps/Consumers
    * Kafka => Kafka Connect => Hadoop/S3/RDBMS => Data Science/Reporting/Audit/Backup/ Storage

### Lecture 98. Case Study - Logging and Metrics Aggregation

* Applications sent logs to kafka topic and kafka connect sink sends it to SPlunk
* same flow for metrics

## Section 14: Kafka in the Enterprise for Admins

### Lecture 99. Kafka Cluster Setup High Level Architecture Overview

* we want multiple brokers in different data centers to distribute the load. we also want a cluster of at least 3 zookeper
* in AWS:
    * us-east-1a: zookeeper1,kafkaBroker1,kafkaBroker4
    * us-east-1b: zookeeper2,kafkaBroker2,kafkaBroker5
    * us-east-1c: zookeeper3,kafkaBroker3,kafkaBroker6
* not easy to setup a cluster
* we want to isolate zookeper and broker on different servers (predictable)
* monitoring needs to be implemented
* operations have to be mastered
* really good kafka admin
* option: KaaS(kafka as a service) pfferings on the web. no operational burden

### Lecture 100. Kafka Monitoring & Operations

* [Confluent](https://docs.confluent.io/current/kafka/monitoring.html)
* [Kafka Monitoring and Operation](https://kafka.apache.org/documentation/#monitoring)
* [Datadog Monitoring](https://www.datadoghq.com/blog/monitoring-kafka-performance-metrics/)
* Kafka exposes metrics through JMX
* Metrics are important to monitor, ensure proper behaviour
* Common places to host Kafka Metrics
    * ELK Stack
    * Datadog
    * Prometheus
    * NewRelic
    * Confluent ControlCenter
* Important Metrics
    * UnderReplicater Partitions: #partitions with problems with ISD. may indicate high load
    * Request handlers: utilization of threads for IO,network overall use of Kafka broker
    * Request Timing: latency in request reply
* Kafka Operations must be able to do:
    * rolling restart of brokers
    * updating configurations
    * rebalancing partitions
    * increasing replication factor
    * add a broker
    * remove a broker
    * replace a broker
    * upgrade a kufka cluster with zero downtime

### Lecture 101. Kafka Security

* withoug security
    * any client can access the cluster (authentication)
    * clients can publish/cosume on any topic (authorization)
    * all data sent visible on network (encryption)
* Threats
    * intercept data
    * publish bad data/steal data
    * delete topics
* Encryption: secure data with SSL like HTTPS. on port 9093
* Authentication: in Kafka only clients that prove identity connect to our Kafka Cluster. login
    * SSL authentication (SSL Cert)
    * SASL authentication (u/p) easy to hack
    * Kerberos: Such as MS Acive Dir (strong -hard to setup)
    * SCRAM: username/pasword (strong -medium difficulty)
* Authorization: w/ authentication
    * ACL (Access Control Lists) have to be maintained by admin
* Best support for kafka Security for apps is Java
* hard to implement

### Lecture 102. Kafka Multi Cluster & MirrorMaker

* [Kafka Mirror Maker](https://cwiki.apache.org/confluence/pages/viewpage.action?pageId=27846330)
* [Mirror Maker Performance Tuning](https://engineering.salesforce.com/mirrormaker-performance-tuning-63afaed12c21)
* [Confluent Replicator](https://docs.confluent.io/current/multi-dc-deployments/replicator/replicator-tuning.html#improving-network-utilization-of-a-connect-task)
* [Kafka Mirror Maker Best Practice](https://community.cloudera.com/t5/Community-Articles/Kafka-Mirror-Maker-Best-Practices/ta-p/249269)
* [Netflix talk on Kafka Replication](https://www.confluent.io/kafka-summit-sf17/multitenant-multicluster-and-hieracrchical-kafka-messaging-service/)
* [Uber UReplicator](https://eng.uber.com/ureplicator-apache-kafka-replicator/)
* [Multi DC Pros and Cons](https://www.altoros.com/blog/multi-cluster-deployment-options-for-apache-kafka-pros-and-cons/)
* [OpenSource KafkaConnect replication](https://github.com/Comcast/MirrorTool-for-Kafka-Connect)
* kafka can only operate well in a single region
* therefore, it is very common for enterprises to have Kafka clusters across the world with some level of replication between them
* a replication application at its core its just a cnsumer+producer
* Tools to do it
    * Mirror Maker - open source tool ships with kafka
    * nteflix uses Flink. htey wrote their own app
    * UBER uses uReplicator - addresses performance and operations issues with MirrorMaker
    * Comcast has their own opensource Kafka Connect Source
    * Confluent has their own Kafka Connect Source (paid)
* 2 designs for cluster replications
* active=>passive: 
    * produces produse data in one cluster and replicated
    * we have a global app
    * we have global dataset
* active=>active: 
    * producers produce data to both clusters. 
    * topics tag data on region
    * we want to have an aggregation cluster (for analytics)
    * desaster recovery strategy (hard)
    * enable cloud migration: on-premise => cloud
* Replicatiing does not preserve offsets just data

### Lecture Section 16: Advanced Topics Configurations

* [Topic Config](https://kafka.apache.org/documentation/#brokerconfigs)
* Brokers have defaults for all the topic configuration parameters
* These parameters impact performance and topic behaviour
* Some topics many have special needs regarding
    * replication factor
    * #of partitions
    * message size
    * compression level
    * log cleanup policy
    * min insync replicas
    * other configurations
* create topic `kafka-topics.sh --bootstrap-server localhost:9092 --create --topic configured-topic --partitions 3 --replication-factor 1`
* we use kafka-config t0 describe the config for a topic `kafka-configs.sh --zookeeper localhost:2181 --entity-type topics --entity-name configured-topic --describe`
* we use `--add-config` passing key value pairs. use `kafka-config.sh` to see them all
* instead of `--describe` we use `--add-config min.insync.replicas=2 --alter` to alter
* to delete `--delete-config min.insync.replicas --alter`

### Lecture 105. Segment and Indexes

* topics are made of partitions
* parttions made of segments(file)
* only one segemnt is ACTIVE (the one data is written to)
* 2 segment settings
    * `log.segment.bytes` the max size of a single segment in bytes
    * `log.segment.ms` the time Kafka will wait before committing the segemnt if not full
* segments come with 2 indexes(files)
    * an offset to position index: allows kafka where to read to find a message
    * a timestamp to offset index: allows Kafka to find mesage with timestamp
* Therefore, Kafka shows where to find data in a constant time
* we can see the files in the dat/kafka folder where kafka outputs each partition in a folder
```
-rw-r--r--  1 achliopa achliopa 10485760 Mar 27 15:39 00000000000000000000.index
-rw-r--r--  1 achliopa achliopa  2999679 Mar 26 22:36 00000000000000000000.log
-rw-r--r--  1 achliopa achliopa 10485756 Mar 27 15:39 00000000000000000000.timeindex
-rw-r--r--  1 achliopa achliopa      240 Mar 27 00:22 00000000000000001283.snapshot
-rw-r--r--  1 achliopa achliopa        8 Mar 27 15:39 leader-epoch-checkpoint
```
* a smaller `log.segment.bytes` (default 1GB) means. 
    * more segments per partition
    * log compaction happens more often
    * BUT Kafka has to keep more files opened (Too many open files)
* Question: how fast will I have new segemnts based on throughputs
* a smaller `log.segment.ms` (time is default 1 week) means:
    * we set a max freq for log compaction (more triggers)

### Lecture 106. Log Cleanup Policies

* many kafka clusters make data expire, accorsing to a policy
* this is called log cleanup
* Policy1: `log.cleanup.policy=delete` kafka default for all user topics
    * Delete based on aget of data (default is 1 week)
    * Delete based on max size of log (default is -1==infinite)
* with `kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic __consumer_offset` we look at policy
* Policy 2: `log.cleanup.policy=compact` kafka default for __consumer_offset
    * delete based on keys of our messages
* Deleting data from kafka allows us to:
    * Control the size of the data on the disk, delete obsolete data
    * Overall: limit mainetnace work on the Cluster
* How often does log cleanup happen?
    * log cleanup happens happens on our parttions segments
    * smaller/more segments means that log cleanup will happen more often
    * log cleanup should not happen too often +> takes up CPU and RAM
    * cleaner checks for work every 15 secs `log.cleaner.backoff.ms`

### Lecture 107. Log Cleanup Delete

* `lof.retention.hours` number of hours to keep data for (default is 168 = 1 week)
    * higher number means more disk space
    * lower number means less data is retained (if our consumers are down too long they can miss data)
* `log.retention.bytes` max size in bytes for each partition (default is -1 infinite)
    * old segemtn will be deleted based on time or space rules
    * new data is written to the active segment
* 2 common pair of options
    * `lof.retention.hours=168` and `log.retention.bytes=-1` for 1 week of retention
    * `lof.retention.hours=17520` and `log.retention.bytes=524288000` for 500MB of data retention

### Lecture 108. Log Compaction Theory

* Log compaction ensures that the log contains at least the last known value for a specific key in a partition
* very  useful if we just require a SNAPSHOT instead of full history (such as for a data table in a DB)
* the idea is that we only keep the latest update for a key in our log
* our topic is: employee-salaray
* we want to keep the most recent salaray for our employees
    * if there is no upadated val for a key val pair in the last segment what happens when the onld segnemtn is deleted?
* any consumer reading from the tail of a log (most current data) will still see all the messages sent to the topic
    * ordering of messages is kept, log compaction only removes some messages, does not reorder them
    * the offset of a message is imutable (if never changes) Offsets are just skipped if a message is missing
    * deleted records can still be seen by consumers for a period of `delete.retention.ms` default is 24hrs
* Myth Busting
* log compacting doesnt prevent us from pushing duplicate data to Kafka
    * de-duplication is done after a segment is commited
    * your consumers will still read from tail as soon as the data arrives
* it doesnt prevent you from reading duplicate data from kafka
    * same points at above
* log compactions can fail from time to time
    * its an optimization and the compaction thread might crash
    * make sure we assign enough memory to it and that it gets triggered
    * restart kafka if log compaction is broken (this is a bug and it may be fixed in future)
* We cant trigger Log Compaction using an API call (for now)
* Log compaction goes to all the old segments apaprt from active one and compacts them to one leaving keys untouched
* log compaction is confifured by `log.cleanup.policy=compact`
    * `segment.ms` *default 7days max amount to wait to close active segments
    * `segment.bytes` default 1G max size of a segment
    * `min.compaction.lag.ms` default 0 how long to wait before a message can be compacted

### Lecture 108. Log Compaction Theory

* 