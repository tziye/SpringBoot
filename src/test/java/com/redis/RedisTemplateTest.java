package com.redis;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.List;

@Slf4j
class RedisTemplateTest extends RedisApplicationTest {

    @Test
    void execute() {
        RedisSerializer ks = redisTemplate.getKeySerializer();
        RedisSerializer vs = redisTemplate.getValueSerializer();
        Object result = redisTemplate.execute((RedisCallback<Object>) connection ->
                connection.set(ks.serialize(K1), vs.serialize("v1")));
        log.info("execute: {}", result);
    }

    @Test
    void executePipelined() {
        RedisSerializer ks = redisTemplate.getKeySerializer();
        RedisSerializer vs = redisTemplate.getValueSerializer();
        List<Object> result = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            connection.multi();
            connection.set(ks.serialize(K1), vs.serialize("v1"));
            connection.set(ks.serialize(K2), vs.serialize("v2"));
            connection.get(ks.serialize(K1));
            connection.get(ks.serialize(K2));
            return connection.exec();
        });
        log.info("executePipelined: {}", result);
    }
}
