package com.apollo.pospublisher.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.apollo.pospublisher.service.KafkaService;

import lombok.extern.slf4j.Slf4j;
import java.util.Map;

@Slf4j
@RequestMapping("/kafka")
@RestController
public class KafkaController {

    @Autowired
    private KafkaService kafkaService;
 
    @RequestMapping(value = "/publish", method = RequestMethod.POST)
    public ResponseEntity<Object> publishKafka(@RequestBody Map<String,Object> params)
	throws Exception {
        // log.info("Publish Kafka: " +params.toString());
        JSONObject paramJsonObject = new JSONObject(params);
        return kafkaService.publishRequestToKafka(paramJsonObject);
    }

}
