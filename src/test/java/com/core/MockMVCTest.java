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
class MockMVCTest extends ApplicationTest {

    @Autowired
    protected MockMvc mvc;

    @Test
    void testMockMvc() throws Exception {
        mvc.perform(get("/param/requestParam?id=1&name=tziye&time=2022-01-15 12:00:00"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"success\":true,\"code\":1,\"data\":\"1 tziye Sat Jan 15 12:00:00 CST 2022\"}"));
    }

    @Test
    void token() throws Exception {
        MvcResult result = this.mvc.perform(get("/security/token")
                .with(httpBasic("admin", "123456")))
                .andExpect(status().isOk())
                .andReturn();

        String token = result.getResponse().getContentAsString();
        log.info("Token: {}", token);

        this.mvc.perform(get("/security/")
                .header("Authorization", "Bearer " + token))
                .andExpect(content().string("Hello, admin!"));
    }
}