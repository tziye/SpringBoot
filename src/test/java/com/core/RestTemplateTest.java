package com.core;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

@Slf4j
class RestTemplateTest extends ApplicationTest {

    @Autowired
    RestTemplate restTemplate;

    @Test
    void test() {
        String result = restTemplate.getForObject("https://www.baidu.com", String.class);
        log.info(result);
    }
}
