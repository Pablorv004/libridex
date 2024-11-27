package com.main.libridex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.main.libridex.entity.User;
import com.main.libridex.service.UserService;


@Controller
@RequestMapping("/profile")
public class ProfileController {

    private static final String PROFILE_VIEW = "profile";
    private static final String PROFILE_EDIT_VIEW = "profile_edit";
    @Autowired
    @Qualifier("userService")
    private UserService userService;

    @GetMapping("")
    public String getProfile(Model model, User user) {
        model.addAttribute("user", userService.findByEmail(((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()));
        return PROFILE_VIEW;
    }

    @GetMapping("/edit")
    public String editProfile(Model model, User user) {
        model.addAttribute("user", userService.findByEmail(((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()));
        return PROFILE_EDIT_VIEW;
    }
    
    
}
