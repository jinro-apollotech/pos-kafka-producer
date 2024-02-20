package com.apollo.pospublisher.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KafkaService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.posTransaction}")
    private String posTransactionTopic;

    
    public ResponseEntity<Object> publishRequestToKafka(JSONObject transaction) {
        try {
            ExecutorService executor = Executors.newFixedThreadPool(10);
            executor.execute(() -> {
                String id = "";
                if(transaction.has("id")){
                    id=transaction.getString("id");
                } else {
                    id=transaction.getString("db_id");
                }
                log.info ("Publishing to kafka topic: {} .. {}", posTransactionTopic,
                id);

                Map<String, Object> map = transaction.toMap();
                ObjectMapper objectMapper = new ObjectMapper();
                String json;
                try {
                    json = objectMapper.writeValueAsString(map);
                    Message<String> message = MessageBuilder.withPayload(json)
                        .setHeader("kafka_messageKey", id)
                        .setHeader(KafkaHeaders.TOPIC, posTransactionTopic).build();
                    kafkaTemplate.send(message);
                    log.debug("Published request to topic {} with key {} : {}",
                    posTransactionTopic, id, transaction);    
                } catch (JsonProcessingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            });

            return new ResponseEntity<Object>("Kafka Publish request processed.", HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error in publishRequestToKafka");
            return new ResponseEntity<Object>("Exception in publishing to Kafka.", HttpStatus.BAD_REQUEST);
        }
    }

}
