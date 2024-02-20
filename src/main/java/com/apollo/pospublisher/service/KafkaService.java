package com.apollo.pospublisher.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KafkaService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.posTransaction}")
    private String posTransactionTopic;

    
    public ResponseEntity<Object> publishRequestToKafka(Map<String, String> transaction) {
        try {
            ExecutorService executor = Executors.newFixedThreadPool(10);
            executor.execute(() -> {
                log.info ("Publishing to kafka topic: {} .. {}", posTransactionTopic,
                transaction);
                Message<Map<String, String>> message = MessageBuilder.withPayload(transaction)
                    .setHeader("kafka_messageKey", transaction.get("id"))
                    .setHeader(KafkaHeaders.TOPIC, posTransactionTopic).build();                
                kafkaTemplate.send(message);
                log.debug("Published request to topic {} with key {} : {}",
                posTransactionTopic, transaction.get("id"),
                transaction);
            });

            return new ResponseEntity<Object>("Kafka Publish request processed.", HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            log.error("Error in publishRequestToKafka");
            return new ResponseEntity<Object>("Exception in publishing to Kafka.", HttpStatus.BAD_REQUEST);
        }
    }

}
