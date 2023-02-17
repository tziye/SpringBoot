package com.redis;

import com.common.util.MyUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.types.Expiration;

import java.util.*;

@Slf4j
class ConnectionTest extends RedisApplicationTest {

    String KEY_PATTERN = K1 + "*";
    RedisConnection connection;

    @BeforeEach
    void setUp() {
        connection = factory.getConnection();
    }

    void mockData(int size) {
        for (var i = 0; i < size; i++) {
            connection.set((K1 + "-" + i).getBytes(), UUID.randomUUID().toString().getBytes(),
                    Expiration.seconds(100), RedisStringCommands.SetOption.UPSERT);
        }
    }

    @Test
    void keys() {
        getKeys();
    }

    List<byte[]> getKeys() {
        mockData(5);
        Set<byte[]> keys = connection.keys(KEY_PATTERN.getBytes());
        List<String> list = new ArrayList<>();
        assert keys != null;
        keys.forEach(key -> list.add(new String(key)));
        log.info("keys: {}", list);
        return new ArrayList<>(keys);
    }

    @Test
    void scan() {
        mockData(5);
        Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match(KEY_PATTERN).build());
        while (cursor.hasNext()) {
            byte[] key = cursor.next();
            log.info("key: {}, type: {}, encoding: {}", new String(key), connection.type(key), connection.encodingOf(key));
        }
    }

    @Test
    void expire() {
        List<byte[]> keys = getKeys();
        byte[] key1 = keys.get(0);
        connection.expire(key1, 2L);
        MyUtil.sleep(2);
        log.info("key1 exists: {}", connection.exists(key1));

        byte[] key2 = keys.get(1);
        connection.expire(key2, 2L);
        MyUtil.sleep(2);
        log.info("key2 ttl: {}", connection.ttl(key2));
        connection.persist(key2);
        log.info("key2 ttl: {}", connection.ttl(key2));
        MyUtil.sleep(2);
        log.info("key2 exists: {}", connection.exists(key2));
    }

    @Test
    void pipeline() {
        long start = System.currentTimeMillis();
        mockData(100000);
        log.info("normal: {}ms", System.currentTimeMillis() - start);
        // 管道
        start = System.currentTimeMillis();
        connection.openPipeline();
        mockData(100000);
        connection.closePipeline();
        log.info("pipeline: {}ms", System.currentTimeMillis() - start);
        // 事务
        start = System.currentTimeMillis();
        connection.multi();
        mockData(100000);
        connection.exec();
        log.info("multi: {}ms", System.currentTimeMillis() - start);
    }

    @Test
    void lock() {
        String key = "Watch-Lock";
        byte[] lock = key.getBytes();
        connection.set(lock, UUID.randomUUID().toString().getBytes());
        // 开启监控
        connection.watch(lock);
        // 监控对象修改导致事务执行失败
        connection.set(lock, UUID.randomUUID().toString().getBytes());
        connection.multi();
        connection.set((key + "-" + 0).getBytes(), UUID.randomUUID().toString().getBytes());
        MyUtil.sleep(2);
        connection.set((key + "-" + 1).getBytes(), UUID.randomUUID().toString().getBytes());
        List<Object> result = connection.exec();
        if (Objects.isNull(result)) {
            log.info("事务执行失败");
        } else {
            log.info("multi: {}", result);
        }
    }

    @Test
    void pubSub() {
        // 订阅
        connection.subscribe((msg, pattern) -> log.info("Receive: {}", msg), K1.getBytes(), K2.getBytes());
        // 发布（广播）
        for (int i = 0; i < 10; i++) {
            connection.publish(K1.getBytes(), (K1 + "-" + i).getBytes());
            MyUtil.sleepRandom(2);
            connection.publish(K2.getBytes(), (K2 + "-" + i).getBytes());
        }
    }

}
