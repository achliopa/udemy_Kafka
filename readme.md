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

* 