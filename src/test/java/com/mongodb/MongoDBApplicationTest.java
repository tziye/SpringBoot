package com.mongodb;

import com.mongodb.client.MongoClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles("unit")
@DataMongoTest
public class MongoDBApplicationTest {

    @Autowired
    public MongoTemplate mongoTemplate;
    @Autowired
    public MongoClient mongoClient;
}
