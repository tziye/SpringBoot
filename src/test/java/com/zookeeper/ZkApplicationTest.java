package com.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;

@Slf4j
@EnableAutoConfiguration(exclude = {
        KafkaAutoConfiguration.class, RabbitAutoConfiguration.class,
        RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class,
        MongoAutoConfiguration.class, MongoRepositoriesAutoConfiguration.class,
        ElasticsearchDataAutoConfiguration.class, ElasticsearchRepositoriesAutoConfiguration.class})
@Import(ZkApplicationTest.ZookeeperConfig.class)
@ActiveProfiles("unit")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ZkApplicationTest {

    @TestConfiguration
    static class ZookeeperConfig {
        @Value("${zookeeper.address}")
        String connectString;

        @Value("${zookeeper.timeout}")
        int timeout;

        @Bean(name = "zk")
        public ZooKeeper zk() {
            ZooKeeper zooKeeper = null;
            try {
                CountDownLatch countDownLatch = new CountDownLatch(1);
                zooKeeper = new ZooKeeper(connectString, timeout, (WatchedEvent event) -> {
                    if (Watcher.Event.KeeperState.SyncConnected == event.getState()) {
                        countDownLatch.countDown();
                    }
                });
                countDownLatch.await();
                log.info("初始化ZooKeeper连接：{}", zooKeeper.getState());
            } catch (Exception e) {
                log.error("初始化ZooKeeper连接异常", e);
            }
            return zooKeeper;
        }
    }

    @Autowired
    ZooKeeper zk;
}