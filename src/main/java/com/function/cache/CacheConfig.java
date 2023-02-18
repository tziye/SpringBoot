package com.function.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String CAFFEINE = "caffeineCacheManager";
    public static final String HAZELCAST = "hazelcastCacheManager";

    @Primary
    @Bean(CAFFEINE)
    public CaffeineCacheManager caffeineCacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder().expireAfterWrite(300L, TimeUnit.SECONDS);
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

    @Qualifier("hazelcastInstance")
    @Autowired
    HazelcastInstance hazelcastInstance;

    @Bean(HAZELCAST)
    public HazelcastCacheManager hazelcastCacheManager() {
        HazelcastCacheManager hazelcastCacheManager = new HazelcastCacheManager();
        hazelcastCacheManager.setHazelcastInstance(hazelcastInstance);
        hazelcastCacheManager.setDefaultReadTimeout(5000);
        return hazelcastCacheManager;
    }

}
