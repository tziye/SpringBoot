package com.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Swagger配置，访问路径http:localhost:9999/swagger-ui.html
 * 默认时间参数需写成以下格式：Mon Oct 29 17:16:04 CST 2018
 */
@Profile({"dev"})
//@EnableSwagger2
@Configuration
public class SwaggerConfig {
//
//    @Bean
//    public Docket createRestApi() {
//        return new Docket(DocumentationType.SWAGGER_2).groupName("springboot").apiInfo(apiInfo()).select()
//                .apis(RequestHandlerSelectors.basePackage("com.controller.basic")).paths(PathSelectors.any()).build();
//    }
//
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder().title("springboot测试接口").build();
//    }

}
