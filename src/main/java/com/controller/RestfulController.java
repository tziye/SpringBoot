package com.controller;

import com.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/rest")
public class RestfulController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/restTemplate")
    public Result<String> restTemplate() {
        String result = restTemplate.getForObject("https://www.baidu.com", String.class);
        return Result.success(result);
    }
}
