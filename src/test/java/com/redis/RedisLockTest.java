package com.redis;

import com.redis.lock.RedisLock;
import com.redis.lock.RedisLocker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RedisLockTest extends RedisApplicationTest {

    @Autowired
    RedisLocker redisLocker;

    @Test
    void testLock() {
        RedisLock lock1 = RedisLock.of("lock");
        boolean rl1 = redisLocker.lock(lock1);
        Assertions.assertTrue(rl1);

        RedisLock lock2 = RedisLock.of("lock");
        boolean rl2 = redisLocker.lock(lock2);
        Assertions.assertFalse(rl2);

        boolean ru1 = redisLocker.unlock(lock1);
        Assertions.assertTrue(ru1);

        boolean ru2 = redisLocker.unlock(lock2);
        Assertions.assertFalse(ru2);

        boolean rl3 = redisLocker.tryLock(lock1);
        Assertions.assertTrue(rl3);
    }
}
