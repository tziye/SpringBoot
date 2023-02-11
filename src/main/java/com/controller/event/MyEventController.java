package com.controller.event;

import com.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event")
public class MyEventController {

    @Autowired
    private ApplicationContext applicationContext;

    @GetMapping("/send")
    public Result<String> send(@RequestParam String message) {
        MyEvent event = new MyEvent(this, message);
        applicationContext.publishEvent(event);
        return Result.success();
    }

}