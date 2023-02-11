package com.controller.websocket;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WebSocketController {

    @GetMapping("/websocket/{username}")
    public String webSocket(@PathVariable String username, Model model) {
        model.addAttribute("username", username);
        return "websocket";
    }
}