package com.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * hazelcast配置
 */
@Configuration
public class HazelcastConfig {

    @Bean
    public Config hazelCastConfig() {
        Config config = new Config();
        config.setInstanceName("spring-hazelcast");
        JoinConfig join = config.getNetworkConfig().getJoin();
        join.getTcpIpConfig().setEnabled(true);
        join.getMulticastConfig().setEnabled(false);
        return config;
    }
}