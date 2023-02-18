package com.redis;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.ZSetOperations;

@Slf4j
class ZSetOperationsTest extends RedisApplicationTest {

    ZSetOperations<String, Object> zSetOpts;

    @BeforeEach
    void setUp() {
        zSetOpts = redisTemplate.opsForZSet();
        for (int i = 1; i <= 10; i++) {
            zSetOpts.add(K1, i, 10 - i);
        }
    }

    @Test
    void score() {
        log.info("score: {}", zSetOpts.score(K1, 3));
        zSetOpts.incrementScore(K1, 3, 2);
        log.info("incrementScore: {}", zSetOpts.score(K1, 3));
    }

    @Test
    void rank() {
        log.info("rank: {}", zSetOpts.rank(K1, 2));
        log.info("reverseRank: {}", zSetOpts.reverseRank(K1, 2));
    }

    @Test
    void pop() {
        log.info("popMax: {}", zSetOpts.popMax(K1, 2));
        log.info("popMin: {}", zSetOpts.popMin(K1, 2));
    }

    @Test
    void remove() {
        zSetOpts.remove(K1, 3);
        log.info("remove: {}", zSetOpts.range(K1, 0, zSetOpts.size(K1)));
        zSetOpts.removeRange(K1, 3, 7);
        log.info("removeRange: {}", zSetOpts.range(K1, 0, zSetOpts.size(K1)));
        zSetOpts.removeRangeByScore(K1, 3, 7);
        log.info("removeRangeByScore: {}", zSetOpts.range(K1, 0, zSetOpts.size(K1)));
    }

    @Test
    void range() {
        log.info("count: {}", zSetOpts.count(K1, 3, 7));
        log.info("rangeWithScores: {}", zSetOpts.rangeWithScores(K1, 3, 7));
        log.info("rangeByScoreWithScores1: {}", zSetOpts.rangeByScoreWithScores(K1, 3, 7));
        log.info("rangeByScoreWithScores2: {}", zSetOpts.rangeByScoreWithScores(K1, 3, 7, 1, 2));
    }

}
