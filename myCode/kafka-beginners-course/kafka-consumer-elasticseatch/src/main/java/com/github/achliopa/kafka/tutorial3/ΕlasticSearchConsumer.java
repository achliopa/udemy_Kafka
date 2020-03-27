package com.github.achliopa.kafka.tutorial3;

import com.google.gson.JsonParser;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ΕlasticSearchConsumer {
    public static RestHighLevelClient createClient() {
        String hostname = "localhost";
        String username = "";
        String password = "";


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

    public static KafkaConsumer<String,String> createConsumer(String topic){
        String bootstrapServers = "127.0.0.1:9092";
        String groupId = "kafka-demo-elasticsearch";
        // create consumer configs
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,"false"); //disable auto ofset commit
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,"100");
        // create consumer
        KafkaConsumer<String,String> consumer = new KafkaConsumer<String, String>(properties);
        // subscribe the consumer
        consumer.subscribe(Arrays.asList(topic));
        return consumer;
    }

    private static JsonParser jsonParser = new JsonParser();

    private static String extractIdFromTweet(String tweetJson){
        // use gson lib
        try {
            return jsonParser.parse(tweetJson)
                    .getAsJsonObject()
                    .get("id_str")
                    .getAsString();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static void main(String[] args) throws IOException {
        Logger logger = LoggerFactory.getLogger(ΕlasticSearchConsumer.class.getName());

        RestHighLevelClient client = createClient();

        KafkaConsumer<String,String> consumer = createConsumer("twitter_tweets");

        // poll for new data
        while(true) {
            ConsumerRecords<String,String> records = consumer.poll(Duration.ofMillis(100));

            Integer recordCount = records.count();
            logger.info("Received "+ recordCount + " records");

            BulkRequest bulkRequest = new BulkRequest();

                for (ConsumerRecord<String,String> record : records) {
                    // 2 strategies
                    // kafka generic ID
    //                String id = record.topic() + "_" +record.partition() + "_" + record.offset();
                    // here we insert data into ES
                    // twitter feed specific id
                    String id = extractIdFromTweet(record.value());
                    if(id!=null){
                        String jsonString = record.value();

                        IndexRequest indexRequest = new IndexRequest(
                                "twitter",
                                "tweets",
                                id //this is to make our consumer idempotent
                        ).source(jsonString, XContentType.JSON);

                        bulkRequest.add(indexRequest); // we add to bulk request (FASTTT)
                        //                IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
                        //                logger.info(indexResponse.getId());
                        //                try {
                        //                    Thread.sleep(10);
                        //                } catch (InterruptedException e) {
                        //                    e.printStackTrace();
                        //                }
                    }
                }
            if(recordCount > 0) {
                BulkResponse bulkResponse = client.bulk(bulkRequest,RequestOptions.DEFAULT);
                logger.info("Commiting offsets...");
                consumer.commitSync();
                logger.info("Offsets have been committed");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        //close the client grcefukk
//        client.close();
    }
}
