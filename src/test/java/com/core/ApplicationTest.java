package com.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@EnableAutoConfiguration(exclude = {
        KafkaAutoConfiguration.class, RabbitAutoConfiguration.class,
        RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class,
        MongoAutoConfiguration.class, MongoRepositoriesAutoConfiguration.class,
        ElasticsearchDataAutoConfiguration.class, ElasticsearchRepositoriesAutoConfiguration.class})
@Slf4j
@ActiveProfiles("unit")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        log.info("setup方法执行了");
    }

    @AfterEach
    public void destroy() {
        log.info("destroy方法执行了");
    }

    protected void log(Object object) {
        try {
            log.info("{}", objectMapper.writeValueAsString(object));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
