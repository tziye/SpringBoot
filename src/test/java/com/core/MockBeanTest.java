package com.core;

import com.function.cache.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

@Slf4j
class MockBeanTest extends ApplicationTest {

    /**
     * 使用@MockBean时，每一个测试方法中如果用到了该bean的方法，都需要mock
     * 根据这一特性，可以mock测试时不需要的类以防止其执行
     */
    @MockBean
    CacheService cacheService1;

    /**
     * 使用@SpyBean只会改变被mock的方法，其他方法还是原来的逻辑
     */
    @SpyBean
    CacheService cacheService2;

    @Test
    void mockBean() {
        Mockito.when(cacheService1.getCacheCf(Mockito.anyInt())).thenReturn(100);
        int result = cacheService1.getCacheCf(1);
        log.info("mock: {}", result);
        Assertions.assertEquals(100, result);
        result = cacheService1.putCache(2, 2);
        log.info("null: {}", result);
    }

    @Test
    void spyBean() {
        Mockito.doReturn(100).when(cacheService2).getCacheCf(Mockito.anyInt());
        int result = cacheService2.getCacheCf(1);
        log.info("mock: {}", result);
        Assertions.assertEquals(100, result);
        result = cacheService2.putCache(2, 2);
        log.info("not null: {}", result);
    }

}
