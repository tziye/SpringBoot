package com.core.util;

import com.core.ApplicationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Properties;

@Slf4j
class SystemEnvTest extends ApplicationTest {

    /**
     * 获取系统环境变量
     */
    @Test
    void getEnv() {
        log.info("获取系统环境变量==========");
        Map<String, String> envMap = System.getenv();
        envMap.entrySet().forEach(entry -> log.info(entry.getKey() + ":" + entry.getValue()));
    }

    /**
     * 获取JVM变量
     */
    @Test
    void getProperties() {
        log.info("获取Jvm变量==========");
        Properties pros = System.getProperties();
        pros.entrySet().forEach(entry -> log.info(entry.getKey() + ":" + entry.getValue()));
    }

}
