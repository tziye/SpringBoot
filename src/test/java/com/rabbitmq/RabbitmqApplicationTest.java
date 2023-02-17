package com.rabbitmq;

import com.common.util.MyUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableAutoConfiguration(exclude = {
        KafkaAutoConfiguration.class,
        RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class,
        MongoAutoConfiguration.class, MongoRepositoriesAutoConfiguration.class,
        ElasticsearchDataAutoConfiguration.class, ElasticsearchRepositoriesAutoConfiguration.class})
@Import(RabbitmqConfig.class)
@ActiveProfiles("unit")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RabbitmqApplicationTest {

    @Autowired
    RabbitmqConfig rabbitmqConfig;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @BeforeEach
    void setup() {
        rabbitmqConfig.declare();
    }

    @Test
    void producer() throws Exception {
        for (int i = 0; i < 1; i++) {
            rabbitTemplate.convertAndSend(RabbitmqConfig.DIRECT_EXCHANGE, RabbitmqConfig.DIRECT_ROUTING_KEY, "这是发往DIRECT_EXCHANGE的第" + i + "条消息");
            MyUtil.sleep(2);

            rabbitTemplate.convertAndSend(RabbitmqConfig.FANOUT_EXCHANGE, null, "这是发往FANOUT_EXCHANGE的第" + i + "条消息");
            MyUtil.sleep(2);

            rabbitTemplate.convertAndSend(RabbitmqConfig.TOPIC_EXCHANGE, RabbitmqConfig.TOPIC_ROUTING_KEY_C, "这是发往TOPIC_EXCHANGE的第" + i + "条消息");
            MyUtil.sleep(2);

            Map<String, Object> map = new HashMap<>();
            map.put(RabbitmqConfig.HEADERS_KEY_1, RabbitmqConfig.HEADERS_VALUE_C_1);
            map.put(RabbitmqConfig.HEADERS_KEY_2, RabbitmqConfig.HEADERS_VALUE_C_2);
            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            messageProperties.setContentType("UTF-8");
            messageProperties.getHeaders().putAll(map);
            Message message = new Message(("这是发往HEADERS_EXCHANGE的第" + i + "条消息").getBytes(), messageProperties);
            rabbitTemplate.convertAndSend(RabbitmqConfig.HEADERS_EXCHANGE, null, message);
            MyUtil.sleep(2);
        }
    }

    @RabbitListener(queuesToDeclare = {
            @Queue(value = RabbitmqConfig.DIRECT_QUEUE),
            @Queue(value = RabbitmqConfig.FANOUT_QUEUE_1),
            @Queue(value = RabbitmqConfig.FANOUT_QUEUE_2),
            @Queue(value = RabbitmqConfig.TOPIC_QUEUE_1),
            @Queue(value = RabbitmqConfig.TOPIC_QUEUE_2),
            @Queue(value = RabbitmqConfig.HEADERS_QUEUE_1),
            @Queue(value = RabbitmqConfig.HEADERS_QUEUE_2)},
            concurrency = "2", autoStartup = "true")
    void consumeDirect(Message message) {
        MessageProperties properties = message.getMessageProperties();
        log.info("Consumer: {}, {}, {}, {}", properties.getConsumerQueue(), properties.getReceivedExchange(),
                properties.getReceivedRoutingKey(), new String(message.getBody()));
    }

}