package com.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

@TestConfiguration
public class RabbitmqConfig implements BeanPostProcessor {

    static final String DIRECT_QUEUE = "myDirectQueue";
    static final String DIRECT_EXCHANGE = "myDirectExchange";
    static final String DIRECT_ROUTING_KEY = "directQueueRoutingKey";
    static final String FANOUT_QUEUE_1 = "myFanoutQueue1";
    static final String FANOUT_QUEUE_2 = "myFanoutQueue2";
    static final String FANOUT_EXCHANGE = "myFanoutExchange";
    static final String TOPIC_QUEUE_1 = "myTopicQueue1";
    static final String TOPIC_QUEUE_2 = "myTopicQueue2";
    static final String TOPIC_EXCHANGE = "myTopicExchange";
    static final String TOPIC_ROUTING_KEY_1 = "topic.*";
    static final String TOPIC_ROUTING_KEY_2 = "topic.#";
    static final String TOPIC_ROUTING_KEY_C = "topic.1.2";
    static final String HEADERS_QUEUE_1 = "myHeadersQueue1";
    static final String HEADERS_QUEUE_2 = "myHeadersQueue2";
    static final String HEADERS_EXCHANGE = "myHeadersExchange";
    static final String HEADERS_KEY_1 = "k1";
    static final String HEADERS_KEY_2 = "k2";
    static final String HEADERS_VALUE_1_1 = "v1";
    static final String HEADERS_VALUE_1_2 = "v2";
    static final String HEADERS_VALUE_2_1 = "va";
    static final String HEADERS_VALUE_2_2 = "vb";
    static final String HEADERS_VALUE_C_1 = "v1";
    static final String HEADERS_VALUE_C_2 = "vb";

    /**
     * Direct Exchange
     */
    @Bean
    Queue directQueue() {
        return new Queue(DIRECT_QUEUE, true);
    }

    @Bean
    DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE);
    }

    @Bean
    Binding directBinding() {
        return BindingBuilder.bind(directQueue()).to(directExchange()).with(DIRECT_ROUTING_KEY);
    }

    /**
     * Fanout Exchange
     */
    @Bean
    Queue fanoutQueue1() {
        return new Queue(FANOUT_QUEUE_1, true);
    }

    @Bean
    Queue fanoutQueue2() {
        return new Queue(FANOUT_QUEUE_2, true);
    }

    @Bean
    FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    @Bean
    Binding fanoutBinding1() {
        return BindingBuilder.bind(fanoutQueue1()).to(fanoutExchange());
    }

    @Bean
    Binding fanoutBinding2() {
        return BindingBuilder.bind(fanoutQueue2()).to(fanoutExchange());
    }

    /**
     * Topic Exchange
     */
    @Bean
    Queue topicQueue1() {
        return new Queue(TOPIC_QUEUE_1, true);
    }

    @Bean
    Queue topicQueue2() {
        return new Queue(TOPIC_QUEUE_2, true);
    }

    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    @Bean
    Binding topicBinding1() {
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with(TOPIC_ROUTING_KEY_1);
    }

    @Bean
    Binding topicBinding2() {
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with(TOPIC_ROUTING_KEY_2);
    }

    /**
     * Headers Exchange
     */
    @Bean
    Queue headersQueue1() {
        return new Queue(HEADERS_QUEUE_1, true);
    }

    @Bean
    Queue headersQueue2() {
        return new Queue(HEADERS_QUEUE_2, true);
    }

    @Bean
    HeadersExchange headersExchange() {
        return new HeadersExchange(HEADERS_EXCHANGE);
    }

    @Bean
    Binding headersBinding1() {
        Map<String, Object> map = new HashMap<>(2);
        map.put(HEADERS_KEY_1, HEADERS_VALUE_1_1);
        map.put(HEADERS_KEY_2, HEADERS_VALUE_1_2);
        return BindingBuilder.bind(headersQueue1()).to(headersExchange()).whereAll(map).match();
    }

    @Bean
    Binding headersBinding2() {
        Map<String, Object> map = new HashMap<>(2);
        map.put(HEADERS_KEY_1, HEADERS_VALUE_2_1);
        map.put(HEADERS_KEY_2, HEADERS_VALUE_2_2);
        return BindingBuilder.bind(headersQueue2()).to(headersExchange()).whereAny(map).match();
    }

    @Bean
    RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    @Autowired
    RabbitAdmin rabbitAdmin;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // declare();
        return null;
    }

    void declare() {
        rabbitAdmin.declareQueue(directQueue());
        rabbitAdmin.declareExchange(directExchange());
        rabbitAdmin.declareQueue(fanoutQueue1());
        rabbitAdmin.declareQueue(fanoutQueue2());
        rabbitAdmin.declareExchange(fanoutExchange());
        rabbitAdmin.declareQueue(topicQueue1());
        rabbitAdmin.declareQueue(topicQueue2());
        rabbitAdmin.declareExchange(topicExchange());
        rabbitAdmin.declareQueue(headersQueue1());
        rabbitAdmin.declareQueue(headersQueue2());
        rabbitAdmin.declareExchange(headersExchange());
    }

}