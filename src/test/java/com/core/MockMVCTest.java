package com.core;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MockMVCTest extends ApplicationTest {

    @Autowired
    protected MockMvc mvc;

    @Test
    void testMockMvc() throws Exception {
        mvc.perform(get("/param/requestParam?id=1&name=tziye&time=2022-01-15 12:00:00"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"success\":true,\"code\":1,\"data\":\"1 tziye Sat Jan 15 12:00:00 CST 2022\"}"));
    }
}