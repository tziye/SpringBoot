package com.function.cache;

import com.common.util.MyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CacheService {

    /**
     * 先从缓存中获取，没有就执行方法，将结果存入缓存
     */
    @Cacheable(cacheManager = CacheConfig.CAFFEINE, cacheNames = "myCache", key = "#key", sync = true)
    public int getCacheCf(int key) {
        return getCache(key);
    }

    @Cacheable(cacheManager = CacheConfig.HAZELCAST, cacheNames = "myCache", key = "#key", sync = true)
    public int getCacheHz(int key) {
        return getCache(key);
    }

    int getCache(int key) {
        log.info("No cache");
        int value = MyUtil.random(100);
        log.info("PutCache: {}-{}", key, value);
        return value;
    }

    /**
     * 每次调用都将符合条件的数据存入缓存，必须返回需要缓存的数据
     */
    @CachePut(cacheManager = CacheConfig.CAFFEINE, cacheNames = "myCache", key = "#key", condition = "#value>50")
    public int putCache(int key, int value) {
        if (value > 50) {
            log.info("PutCache: {}-{}", key, value);
        }
        return value;
    }

    /**
     * 删除缓存数据，可全部删除，也可指定key
     */
    @CacheEvict(cacheManager = CacheConfig.CAFFEINE, cacheNames = "myCache", allEntries = true)
    public void cacheEvict() {
        log.info("Evict cache");
    }

}
