package com.redis;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.ListOperations;

@Slf4j
public class ListOperationsTest extends RedisApplicationTest {

    ListOperations<String, Object> listOpts;

    @BeforeEach
    void setUp() {
        listOpts = redisTemplate.opsForList();
    }

    @Test
    void queue() {
        listOpts.rightPushAll(K1, 1, 2, 3, 4, 5);
        while (true) {
            Object o = listOpts.leftPop(K1);
            log.info("queue: {}", o);
            if (o == null) {
                break;
            }
        }
    }

    @Test
    void stack() {
        listOpts.rightPushAll(K1, 1, 2, 3, 4, 5);
        while (true) {
            Object o = listOpts.rightPop(K1);
            log.info("stack: {}", o);
            if (o == null) {
                break;
            }
        }
    }

    @Test
    void combine() {
        listOpts.rightPushAll(K1, 1, 2, 3, 4, 5);
        while (true) {
            Object o = listOpts.rightPopAndLeftPush(K1, K2);
            if (o == null) {
                break;
            }
        }
        log.info("source: {}", listOpts.range(K1, 0, listOpts.size(K1)));
        log.info("destination: {}", listOpts.range(K2, 0, listOpts.size(K2)));
    }

    @Test
    void index() {
        listOpts.rightPushAll(K1, 1, 2, 3, 4, 5);
        log.info("index: {}", listOpts.index(K1, 3));
        listOpts.set(K1, 3, 10);
        log.info("set: {}", listOpts.index(K1, 3));
    }

    @Test
    void remove() {
        listOpts.rightPushAll(K1, 1, 1, 1, 1, 1);
        listOpts.remove(K1, 2, 1);
        log.info("remove: {}", listOpts.range(K1, 0, listOpts.size(K1)));
    }

    @Test
    void trim() {
        listOpts.rightPushAll(K1, 1, 2, 3, 4, 5);
        listOpts.trim(K1, 1, 3);
        log.info("trim: {}", listOpts.range(K1, 0, listOpts.size(K1)));
    }
}
