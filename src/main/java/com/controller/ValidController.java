package com.controller;

import com.common.Result;
import com.common.aspect.WebAuth;
import com.pojo.vo.UserVo;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 参数校验测试
 */
@Validated // 启动param校验
@RestController
@RequestMapping("/valid")
public class ValidController {

    @PostMapping("/validVo")
    public Result<UserVo> validVo(@Valid @RequestBody UserVo userVo) {
        return Result.success(userVo);
    }

    @PostMapping("/validParam")
    public Result<String> validParam(
            @Range(min = 0, max = 10000, message = "id必须介于0-10000") @RequestParam("id") long id,
            @Length(min = 1, max = 10, message = "用户名长度必须在1-10之间") @RequestParam("name") String name) {
        return Result.success(id + ":" + name);
    }

    @WebAuth("Forbid")
    @PostMapping("/validAuth")
    public Result<String> validAuth() {
        return Result.success();
    }
}
