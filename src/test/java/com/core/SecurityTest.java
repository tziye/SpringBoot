package com.core;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
class SecurityTest extends ApplicationTest {

    @Autowired
    MockMvc mvc;

    @Test
    void test() throws Exception {
        MvcResult result = mvc.perform(get("/security/token")
                .with(httpBasic("admin", "123456")))
                .andExpect(status().isOk())
                .andReturn();

        String token = result.getResponse().getContentAsString();
        log.info("Token: {}", token);

        mvc.perform(get("/security/hello")
                .header("Authorization", "Bearer " + token))
                .andExpect(content().string("Hello, admin!"));
    }
}