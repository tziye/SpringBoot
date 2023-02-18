package com.core;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.net.InetAddress;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
class PropertyTest extends ApplicationTest {

    @EnableConfigurationProperties(AcmeBo.class)
    @PropertySource(value = {"classpath:setting.yaml"}, encoding = "utf-8")
    @TestConfiguration
    static class ResourceConfig {
    }

    @Value("${value}")
    String value;

    @Autowired
    Environment environment;

    @Autowired
    AcmeBo acmeBo;

    @Test
    void test() {
        log.info(value);
        log.info(environment.getProperty("value"));
        log.info("{}", acmeBo);
    }

    @Data
    @Validated
    @ConfigurationProperties("acme")
    public static class AcmeBo {
        @NotNull
        boolean enabled;
        InetAddress remoteAddress;
        Security security = new Security();

        @Data
        public static class Security {
            String username;
            String password;
            List<String> roles = new ArrayList<>(Collections.singleton("USER"));
        }

        @DurationUnit(ChronoUnit.SECONDS)
        Duration sessionTimeout = Duration.ofSeconds(30);
        @DataSizeUnit(DataUnit.MEGABYTES)
        DataSize bufferSize = DataSize.ofMegabytes(2);
    }

}
