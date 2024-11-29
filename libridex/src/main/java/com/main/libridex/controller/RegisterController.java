package com.main.libridex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.main.libridex.converters.UserMapper;
import com.main.libridex.model.UserDTO;
import com.main.libridex.service.impl.UserServiceImpl;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/register")
public class RegisterController {
    private static final String REGISTER_VIEW = "register";
    private static final String SUCCESS_MESSAGE="User registered! Please proceed to login.";

    @Autowired
    @Qualifier("userService")
    private UserServiceImpl userService;

    @GetMapping("")
    public String getRegister(Model model) {
        model.addAttribute("user", new UserDTO());
        return REGISTER_VIEW;
    }

    @PostMapping("/send")
    public String registerUser(@Valid @ModelAttribute("user") UserDTO userDTO, BindingResult bindingResult, RedirectAttributes flash) {
        if (!userService.isRegisterValid(userDTO, bindingResult))
            return REGISTER_VIEW;
        userService.register(UserMapper.toUser(userDTO));
        flash.addFlashAttribute("success", SUCCESS_MESSAGE);
        return "redirect:/login";
    }
}
