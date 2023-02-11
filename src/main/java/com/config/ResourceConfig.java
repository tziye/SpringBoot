package com.config;

import com.pojo.bo.AcmeBo;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties(AcmeBo.class)
@PropertySource(value = {"classpath:setting.yaml"}, encoding = "utf-8")
@ImportResource(locations = {"classpath:bean.xml"})
public class ResourceConfig {

}
