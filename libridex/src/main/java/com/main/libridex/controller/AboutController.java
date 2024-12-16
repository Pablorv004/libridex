package com.main.libridex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.main.libridex.components.logger.AccessLogger;

@Controller
public class AboutController {

    private static final String ABOUT = "about";

    @Autowired
    @Qualifier("accessLogger")
    private AccessLogger accessLogger;

    @GetMapping("/about")
    public String about(Model model) {
        accessLogger.accessed("about");
        return ABOUT;
    }
}