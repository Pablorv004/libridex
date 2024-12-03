package com.main.libridex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.main.libridex.components.logger.AccessLogger;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final String ADMIN_VIEW = "admin";

    @Autowired
    @Qualifier("accessLogger")
    private AccessLogger accessLogger;

    @GetMapping("")
    public String getMainMenuView() {
        return ADMIN_VIEW;
    }
    
    
}
