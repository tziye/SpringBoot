package com.controller.basic;

import com.common.Result;
import com.pojo.entity.GenderEnum;
import com.pojo.vo.UserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Date;

/**
 * 响应测试入口
 */
@Api("响应测试入口")
@Controller
@RequestMapping("/response")
public class ResponseController {

    @ApiOperation("重定向到视图")
    @GetMapping("/")
    public RedirectView home() {
        // 重定向到视图，true表示路径是相对于上下文，而不是服务器，常用于重定向到其他controller
        return new RedirectView("/response/board", true);
    }

    @ApiOperation("转发到视图")
    @GetMapping("/board")
    public String board(Model model) { // model作用类似于共享域
        model.addAttribute("text", "This is a model attribute.");
        model.addAttribute("date", new Date());
        return "basic";
    }

    @ApiOperation("返回封装的实体1")
    @GetMapping("/user1/{userId}")
    public ResponseEntity<UserVo> getUser1(@PathVariable("userId") int userId) {
        UserVo user = new UserVo(userId, "john", 35, GenderEnum.BOY, true, new Date());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @ApiOperation("返回封装的实体2")
    @GetMapping("/user2/{userId}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Result<UserVo> getUser2(@PathVariable("userId") int userId) {
        UserVo user = new UserVo(userId, "john", 25, GenderEnum.BOY, true, new Date());
        return Result.success(user);
    }

}
