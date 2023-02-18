package com.function.basic;

import com.common.Result;
import com.dto.UserDto;
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
@Controller
@RequestMapping("/response")
public class ResponseController {

    @GetMapping("/")
    public RedirectView home() {
        // 重定向到视图，true表示路径是相对于上下文，而不是服务器，常用于重定向到其他controller
        return new RedirectView("/response/board", true);
    }

    @GetMapping("/board")
    public String board(Model model) {
        // model作用类似于共享域
        model.addAttribute("text", "This is a model attribute.");
        model.addAttribute("date", new Date());
        return "basic";
    }

    @GetMapping("/user1/{userId}")
    public ResponseEntity<UserDto> getUser1(@PathVariable("userId") int userId) {
        UserDto user = new UserDto(userId, "john", 35, "Boy", true, new Date());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/user2/{userId}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Result<UserDto> getUser2(@PathVariable("userId") int userId) {
        UserDto user = new UserDto(userId, "john", 25, "Girl", true, new Date());
        return Result.success(user);
    }

}
