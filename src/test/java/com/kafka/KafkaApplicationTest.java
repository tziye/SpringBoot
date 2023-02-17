package com.kafka;

import com.common.util.MyUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

@Slf4j
@EnableAutoConfiguration(exclude = {
        RabbitAutoConfiguration.class,
        RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class,
        MongoAutoConfiguration.class, MongoRepositoriesAutoConfiguration.class,
        ElasticsearchDataAutoConfiguration.class, ElasticsearchRepositoriesAutoConfiguration.class})
@EmbeddedKafka
@TestPropertySource(properties = {"spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"})
@Import(KafkaApplicationTest.KafkaConfig.class)
@ActiveProfiles("unit")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class KafkaApplicationTest {

    static final String TOPIC = "my-topic";

    @TestConfiguration
    static class KafkaConfig {

        @Resource
        EmbeddedKafkaBroker broker;

        @Bean
        public KafkaTemplate<Integer, String> template() {
            return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(KafkaTestUtils.producerProps(broker)));
        }
    }

    @Autowired
    KafkaTemplate<Integer, String> kafkaTemplate;

    @Test
    void produce() throws Exception {
        InputStream is = new FileInputStream("src/main/resources/static/head.jpg");
        BufferedImage image = ImageIO.read(is);
        ByteArrayOutputStream iOut = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", iOut);
        byte[] pic = iOut.toByteArray();
        is.close();

        for (int i = 0; i < 10; i++) {
            KafkaMsg msg = new KafkaMsg("这是第" + i + "条消息！！！", pic, i);
            ProducerRecord<Integer, String> record = new ProducerRecord<>(TOPIC, 0, i, MyUtil.toString(msg));
            ListenableFuture<SendResult<Integer, String>> future = kafkaTemplate.send(record);
            RecordMetadata metadata = future.get().getRecordMetadata();
            log.info("发送消息：{}，分区：{}，偏移量：{}", msg.getMsg(), metadata.partition(), metadata.offset());
            MyUtil.sleep(2);
        }
    }

    @KafkaListener(id = "client1", topics = {TOPIC}, concurrency = "2", topicPartitions = {
            @TopicPartition(topic = TOPIC, partitions = {"0"})})
    void kafkaListener(ConsumerRecord<String, String> record) throws Exception {
        KafkaMsg msg = MyUtil.parse(record.value(), KafkaMsg.class);
        log.info("收到消息：{}，分区：{}，偏移量：{}", msg.getMsg(), record.partition(), record.offset());
        ByteArrayInputStream in = new ByteArrayInputStream(msg.getPic());
        BufferedImage image = ImageIO.read(in);
        OutputStream bOut = new FileOutputStream("target/" + msg.getSeq() + ".jpg");
        ImageIO.write(image, "jpg", bOut);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class KafkaMsg {
        String msg;
        byte[] pic;
        int seq;
    }

}
