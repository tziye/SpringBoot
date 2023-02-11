package com.controller.basic;

import com.common.Result;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

/**
 * 请求测试入口
 */
//@Api("请求测试入口")
@RestController
@RequestMapping("/request")
public class RequestController {

//    @ApiOperation("控制请求方法")
    @RequestMapping(value = "/method", method = RequestMethod.GET)
    public Result<String> method() {
        return Result.success();
    }

//    @ApiOperation("控制请求参数")
    @GetMapping(value = "/param", params = {"name", "age=23"})
    public Result<String> param(@RequestParam("name") String name,
                                @RequestParam("age") int age) {
        return Result.success(name + " " + age);
    }

//    @ApiOperation("控制请求头")
    @GetMapping(value = "/header", headers = {"content-type=text/*"})
    public Result<String> header() {
        return Result.success();
    }

//    @ApiOperation("控制请求内容类型")
    @PostMapping(value = "/contentType", consumes = {"application/json"}, produces = {"application/json"})
    public Result<String> contentType() {
        return Result.success();
    }

}
