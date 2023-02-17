package com.redis;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
class ValueOperationsTest extends RedisApplicationTest {

    ValueOperations<String, Object> valueOpts;

    @BeforeEach
    void setUp() {
        valueOpts = redisTemplate.opsForValue();
    }

    @Test
    void setGetDel() {
        valueOpts.set(K1, "v1");
        Object list = valueOpts.get(K1);
        log.info("get: {}", list);
        redisTemplate.delete(K1);
        log.info("hasKey: {}", redisTemplate.hasKey(K1));
    }

    @Test
    void multiSetGet() {
        Map<String, Object> map = new HashMap<>();
        map.put(K1, "v1");
        map.put(K2, "v2");
        valueOpts.multiSet(map);

        List<Object> list = valueOpts.multiGet(Arrays.asList(K1, K2));
        log.info("multiGet: {}", list);
    }

    @Test
    void incr() {
        valueOpts.set(K1, 1);
        log.info("increment: {}", valueOpts.increment(K1, 3));
        log.info("decrement: {}", valueOpts.decrement(K1, 2));
    }

    @Test
    void bit() {
        valueOpts.setBit(K1, 5, true);
        log.info("getBit: {}", valueOpts.getBit(K1, 5));
    }

}
