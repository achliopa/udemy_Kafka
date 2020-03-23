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
