package com.redis;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.SetOperations;

@Slf4j
class SetOperationsTest extends RedisApplicationTest {

    SetOperations<String, Object> setOpts;

    @BeforeEach
    void setUp() {
        setOpts = redisTemplate.opsForSet();
    }

    @Test
    void addPopRemove() {
        setOpts.add(K1, 1, 2, 3, 4, 5);
        log.info("members: {}", setOpts.members(K1));
        log.info("size: {}", setOpts.size(K1));

        setOpts.remove(K1, 4);
        log.info("isMember: {}", setOpts.isMember(K1, 4));

        log.info("pop: {}", setOpts.pop(K1, 2));
    }

    @Test
    void calculate() {
        String store = "store";
        setOpts.add(K1, 1, 2, 3, 4, 5);
        setOpts.add(K2, 4, 5, 6, 7, 8);
        log.info("difference: {}", setOpts.difference(K1, K2));
        setOpts.differenceAndStore(K1, K2, store);
        log.info("differenceAndStore: {}", setOpts.members(store));

        log.info("intersect: {}", setOpts.intersect(K1, K2));
        setOpts.intersectAndStore(K1, K2, store);
        log.info("intersectAndStore: {}", setOpts.members(store));

        log.info("union: {}", setOpts.union(K1, K2));
        setOpts.unionAndStore(K1, K2, store);
        log.info("unionAndStore: {}", setOpts.members(store));
    }

    @Test
    void random() {
        setOpts.add(K1, 1, 2, 3, 4, 5);
        log.info("randomMember: {}", setOpts.randomMember(K1));
        log.info("randomMembers: {}", setOpts.randomMembers(K1, 4));
        log.info("distinctRandomMembers: {}", setOpts.distinctRandomMembers(K1, 4));
    }
}
