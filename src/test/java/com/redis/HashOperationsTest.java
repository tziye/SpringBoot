package com.redis;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.HashOperations;

import java.util.HashMap;
import java.util.Map;

@Slf4j
class HashOperationsTest extends RedisApplicationTest {

    HashOperations<String, String, Object> hashOpts;

    @BeforeEach
    void setUp() {
        hashOpts = redisTemplate.opsForHash();
    }

    @Test
    void putGetDel() {
        hashOpts.put(K1, K2, "v1");
        log.info("get: {}", hashOpts.get(K1, K2));
        hashOpts.delete(K1, K2);
        log.info("hasKey: {}", hashOpts.hasKey(K1, K2));
    }

    @Test
    void multiPutGet() {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            map.put("t" + i, i);
        }
        hashOpts.putAll(K1, map);
        log.info("multiGet: {}", hashOpts.multiGet(K1, map.keySet()));
        log.info("entries: {}", hashOpts.entries(K1));
        log.info("keys: {}", hashOpts.keys(K1));
        log.info("values: {}", hashOpts.values(K1));
        log.info("size: {}", hashOpts.size(K1));
    }

    @Test
    void increment() {
        hashOpts.put(K1, K2, 1);
        hashOpts.increment(K1, K2, 3);
        log.info("increment: {}", hashOpts.get(K1, K2));
        hashOpts.increment(K1, K2, -2);
        log.info("decrement: {}", hashOpts.get(K1, K2));
    }

}
