package com.config;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ServletComponentScan // 用于发现@WebServlet、@WebFilter、@WebListener
public class ServletConfig {
}
