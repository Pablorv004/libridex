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

import com.main.libridex.components.logger.AccessLogger;
import com.main.libridex.components.logger.UserLogger;
import com.main.libridex.model.UserDTO;
import com.main.libridex.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/register")
public class RegisterController {
    private static final String REGISTER_VIEW = "register";
    private static final String SUCCESS_MESSAGE="User registered! Please proceed to login.";

    @Autowired
    @Qualifier("userService")
    private UserService userService;

    @Autowired
    @Qualifier("accessLogger")
    private AccessLogger accessLogger;

    @Autowired
    @Qualifier("userLogger")
    private UserLogger userLogger;

    @GetMapping("")
    public String getRegister(Model model) {
        model.addAttribute("user", new UserDTO());
        accessLogger.accessed("register");
        return REGISTER_VIEW;
    }

    @PostMapping("/send")
    public String registerUser(@Valid @ModelAttribute("user") UserDTO userDTO, BindingResult bindingResult, RedirectAttributes flash) {
        if (!userService.isRegisterValid(userDTO, bindingResult)){
            userLogger.failedToRegister(userDTO.getEmail(), new Exception("Invalid registration data"));
            return REGISTER_VIEW;
        }
        userService.register(userService.toEntity(userDTO));
        userLogger.registered(userDTO.getEmail());
        flash.addFlashAttribute("success", SUCCESS_MESSAGE);
        return "redirect:/login";
    }
}
