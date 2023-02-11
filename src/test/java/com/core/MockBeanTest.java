package com.core;

import com.common.Result;
import com.controller.CacheController;
import com.controller.JpaController;
import com.pojo.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Collections;
import java.util.List;

@Slf4j
class MockBeanTest extends ApplicationTest {

    /**
     * 使用@MockBean时，每一个测试方法中如果用到了该bean的方法，都需要mock
     * 根据这一特性，可以mock测试时不需要的类以防止其执行
     */
    @MockBean
    private CacheController cacheController;

    /**
     * @SpyBean只会改变被mock的方法，其他方法还是原来的逻辑
     */
    @SpyBean
    private JpaController jpaController;

    @Test
    void testMockBean() {
        Mockito.when(cacheController.cacheable(Mockito.anyLong(), Mockito.anyString())).thenReturn(Result.success("2000:html"));
        Result<String> result = cacheController.cacheable(1000, "xml");
        log.info("{}", result);
        Assertions.assertEquals("2000:html", result.getData());
    }

    @Test
    void testSpyBean() {
        User user = new User();
        user.setName("test");
        Mockito.when(jpaController.findByNameAndAge(Mockito.anyString(), Mockito.anyInt())).thenReturn(Result.success(Collections.singletonList(user)));
        Result<List<User>> result = jpaController.findByNameAndAge("", 0);
        log.info("{}", result);
        Assertions.assertEquals("test", result.getData().get(0).getName());
    }

}
