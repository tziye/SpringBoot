package com.redis.lock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedisLock {

     boolean locked;
     String key;
     String value;
     int expireTime;
     int timeout;

    public static RedisLock of(String key) {
        RedisLock lock = new RedisLock();
        lock.setKey(key);
        lock.setValue(UUID.randomUUID().toString());
        lock.setExpireTime(60);
        lock.setTimeout(3);
        return lock;
    }
}
