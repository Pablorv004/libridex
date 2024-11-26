package com.main.libridex.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.main.libridex.entity.User;

@Controller
@RequestMapping("/login")
public class LoginController {
    private static final String LOGIN_VIEW = "login";

    @GetMapping("")
    public String login(Model model, @RequestParam(value = "error", required = false) String error, @RequestParam(value = "logout", required = false) String logout) {
        model.addAttribute("error", error);
        model.addAttribute("logout", logout);
        model.addAttribute("user", new User());
        return LOGIN_VIEW;
    }
    
}
