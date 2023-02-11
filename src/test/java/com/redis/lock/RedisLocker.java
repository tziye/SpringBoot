package com.redis.lock;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RedisLocker {

    @Autowired(required = false)
     RedisTemplate<String, Object> redisTemplate;

    /**
     * 加锁
     */
    public boolean lock(RedisLock redisLock) {
        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(redisLock.getKey(), redisLock.getValue(), redisLock.getExpireTime(), TimeUnit.SECONDS);
        log.info("Lock Result: {}, RedisLock: {}", locked, redisLock);
        return BooleanUtils.isTrue(locked);
    }

    /**
     * 持续性加锁
     */
    public boolean tryLock(RedisLock redisLock) {
        boolean locked = false;
        try {
            long start = System.currentTimeMillis();
            while (true) {
                locked = lock(redisLock);
                if (locked || System.currentTimeMillis() - start > (redisLock.getTimeout()) * 1000) {
                    break;
                }
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return locked;
    }

    /**
     * 解锁
     */
    public boolean unlock(RedisLock redisLock) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setLocation(new ClassPathResource("unlock.lua"));
        redisScript.setResultType(Long.class);
        Long result = redisTemplate.execute(redisScript, Collections.singletonList(redisLock.getKey()), redisLock.getValue());
        boolean unlocked = Objects.equals(result, 1L);
        log.info("Unlock Result: {}, RedisLock: {}", unlocked, redisLock);
        return unlocked;
    }

}
