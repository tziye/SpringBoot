package com.core;

import com.function.cache.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
class CacheTest extends ApplicationTest {

    @Autowired
    CacheService cacheService;

    @Test
    void getCache() {
        int v1 = cacheService.getCacheCf(1);
        int v2 = cacheService.getCacheCf(1);
        Assertions.assertEquals(v1, v2);
        cacheService.cacheEvict();
        v2 = cacheService.getCacheCf(1);
        Assertions.assertNotEquals(v1, v2);
    }

    @Test
    void getPut() {
        cacheService.putCache(2, 70);
        int v1 = cacheService.getCacheCf(2);
        Assertions.assertEquals(70, v1);
        cacheService.cacheEvict();
        cacheService.putCache(2, 1);
        v1 = cacheService.getCacheCf(2);
        Assertions.assertNotEquals(1, v1);
    }

    @Test
    void cacheManager() {
        int v1 = cacheService.getCacheCf(1);
        int v2 = cacheService.getCacheHz(1);
        Assertions.assertNotEquals(v1, v2);
    }
}
