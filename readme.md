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