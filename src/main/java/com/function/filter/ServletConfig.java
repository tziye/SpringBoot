package com.function.filter;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 用于发现@WebServlet、@WebFilter、@WebListener
 */
@ServletComponentScan
@Configuration
public class ServletConfig {
}
