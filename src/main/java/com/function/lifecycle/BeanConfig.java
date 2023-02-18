package com.function.lifecycle;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public int id() {
        return 0;
    }

    @Bean
    public String value() {
        return "hello";
    }

    @Bean(initMethod = "initMethod", destroyMethod = "destroyMethod")
    public BeanLifeCycle beanLifeCycle() {
        return new BeanLifeCycle(id(), value());
    }

}
