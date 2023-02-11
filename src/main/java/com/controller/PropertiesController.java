package com.controller;

import com.common.Result;
import com.pojo.bo.AcmeBo;
import com.pojo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/properties")
public class PropertiesController {

    @Autowired
    private User userBean;

    @Autowired
    private AcmeBo acmeBo;

    @Value("${value}")
    private String value;

    @Autowired
    private Environment environment;

    @GetMapping("/userBean")
    public Result<User> userBean() {
        return Result.success(userBean);
    }

    @GetMapping("/acme")
    public Result<AcmeBo> acme() {
        return Result.success(acmeBo);
    }

    @GetMapping("/value")
    public Result<String> value() {
        return Result.success(value);
    }

    @GetMapping("/env")
    public Result<String> env() {
        return Result.success(environment.getProperty("value"));
    }

}