package com.function.basic;

import com.common.Result;
import com.dto.UserDto;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * 请求测试入口
 */
@RestController
@RequestMapping("/request")
public class RequestController {

    @RequestMapping(value = "/limit", method = RequestMethod.GET,
            params = {"name", "age=18"}, headers = {"myHeader=big"},
            consumes = {"application/json"}, produces = {"application/json"})
    public Result<String> method(@RequestParam("name") String name, @RequestParam("age") int age) {
        return Result.success(name + " " + age);
    }

    @GetMapping("/servlet")
    public Result<String> servletParam(HttpServletRequest request, HttpServletResponse response) {
        return Result.success(request + " " + response);
    }

    @GetMapping("/param")
    public Result<String> requestParam(@RequestParam("id") int id,
                                       @RequestParam(value = "name", required = false) String name,
                                       @RequestParam(value = "time") Date time) {
        return Result.success(id + " " + name + " " + time);
    }

    @GetMapping("/paramObject")
    public Result<UserDto> requestParamObject(UserDto user) {
        return Result.success(user);
    }

    @GetMapping("/url/{p1}/{p2}")
    public Result<String> urlParam(@PathVariable("p1") String p1, @PathVariable("p2") String p2) {
        return Result.success(p1 + " " + p2);
    }

    @GetMapping("/urlMap/{p1}/{p2}")
    public Result<Map<String, String>> urlParamMap(@PathVariable Map<String, String> variables) {
        return Result.success(variables);
    }

    @GetMapping("/header")
    public Result<String> headerParam(@RequestHeader("Content-Type") String contentType,
                                      @RequestHeader(value = "X-Custom-Header", required = false) Date customHeader) {
        return Result.success(contentType + " " + customHeader);
    }

    @PostMapping("/form")
    public Result<UserDto> formParam(UserDto user) {
        return Result.success(user);
    }

    @PostMapping("/json")
    public Result<UserDto> contentParam(@RequestBody UserDto user) {
        return Result.success(user);
    }

    @PostMapping("/entity")
    public Result<HttpEntity<UserDto>> requestParam(HttpEntity<UserDto> request) {
        return Result.success(request);
    }

}
