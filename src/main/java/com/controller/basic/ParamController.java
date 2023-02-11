package com.controller.basic;

import com.common.Result;
import com.pojo.vo.UserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * 请求参数测试入口
 */
@Api("请求参数测试入口")
@RestController
@RequestMapping("/param")
public class ParamController {

    @ApiOperation("获取Request和Response")
    @GetMapping("/servletParam")
    public Result<String> servletParam(HttpServletRequest request, HttpServletResponse response) {
        return Result.success(request + " " + response);
    }

    @ApiOperation("获取请求参数")
    @GetMapping("/requestParam")
    public Result<String> requestParam(@RequestParam("id") int id,
                                       @RequestParam(value = "name", required = false) String name,
                                       @RequestParam(value = "time") Date time) {
        return Result.success(id + " " + name + " " + time);
    }

    @ApiOperation("获取请求参数对象")
    @GetMapping("/requestParamObject")
    public Result<UserVo> requestParamObject(UserVo user) {
        return Result.success(user);
    }

    @ApiOperation("从URL中获取参数")
    @GetMapping("/urlParam/{param1}/{param2}")
    public Result<String> urlParam(@PathVariable("param1") String param1, @PathVariable("param2") String param2) {
        return Result.success(param1 + " " + param2);
    }

    @ApiOperation("通过Map从URL中获取参数")
    @GetMapping("/urlParamMap/{param1}/{param2}")
    public Result<Map<String, String>> urlParamMap(@PathVariable Map<String, String> variables) {
        return Result.success(variables);
    }

    @ApiOperation("读取请求头参数")
    @GetMapping("/headerParam")
    public Result<String> headerParam(@RequestHeader("Content-Type") String contentType,
                                      @RequestHeader(value = "X-Custom-Header", required = false) Date customHeader) {
        return Result.success(contentType + " " + customHeader);
    }

    @ApiOperation("将post提交的表单转换为entity")
    @PostMapping("/formParam")
    public Result<UserVo> formParam(UserVo user) {
        return Result.success(user);
    }

    @ApiOperation("将post提交的json转换为entity")
    @PostMapping("/contentParam")
    public Result<UserVo> contentParam(@RequestBody UserVo user) {
        return Result.success(user);
    }

    @ApiOperation("将整个post请求封装为对象，实体内容为json")
    @PostMapping("/allRequestParam")
    public Result<HttpEntity<UserVo>> requestParam(HttpEntity<UserVo> request) {
        return Result.success(request);
    }

}
