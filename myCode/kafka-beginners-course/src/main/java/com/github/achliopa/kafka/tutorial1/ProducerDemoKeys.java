package com.github.achliopa.kafka.tutorial1;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class ProducerDemoKeys {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        final Logger logger = LoggerFactory.getLogger(ProducerDemoKeys.class.getName());

        // create Producer properties
        Properties properties = new Properties();
        String bootstrapServers = "127.0.0.1:9092";
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());
        // create Producer
        KafkaProducer<String,String> producer = new KafkaProducer<String, String>(properties);

        for(int i=0;i<10;i++){
            // create a ProducerRecord
            String topic = "first_topic";
            String value = "hello world " + Integer.toString(i);
            final String key = "id_" + Integer.toString(i%3);
            ProducerRecord<String,String> record = new ProducerRecord<String, String>(topic,key,value);

            // send data - asynchronous
            producer.send(record, new Callback() {
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    // executes every time a record is successfully sent or an exception is thrown
                    if (e == null) {
                        // the record was successfully sent
                        logger.info("\nKey: " + key + " [Received new metadata] "+
                                "Topic: " + recordMetadata.topic() +
                                " Partition: " + recordMetadata.partition() +
                                " Offset: " + recordMetadata.offset() +
                                " Timestamp: " + recordMetadata.timestamp());
                    } else {
                        logger.error("Error while producing: " + e);
                    }
                }
            }).get(); // block the .send() to make it synchronous - don't do this in production
        }

        //flush
        producer.flush();
        //flush and close
        producer.close();
    }
}
