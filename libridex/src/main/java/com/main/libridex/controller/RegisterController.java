package com.main.libridex.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.main.libridex.entity.User;


@Controller
@RequestMapping("/register")
public class RegisterController {
    private static final String REGISTER_VIEW = "register";
    
    @GetMapping("")
    public String getRegister(Model model, @RequestParam(value = "error", required = false) String error) {
        model.addAttribute("error", error);
        model.addAttribute("user", new User());
        return REGISTER_VIEW;
    }
    
}
