package com.main.libridex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.main.libridex.components.logger.AccessLogger;
import com.main.libridex.service.BookService;

@Controller
public class IndexController {

    private static final String INDEX = "index";

    @Autowired
    @Qualifier("accessLogger")
    private AccessLogger accessLogger;
    
    @Autowired
    @Qualifier("bookService")
    private BookService bookService;

    @GetMapping("/")
    public String redirectToIndex() {
        accessLogger.redirected("index");
        return "redirect:/index";
    }

    @GetMapping("/index")
    public ModelAndView index(){
        ModelAndView mav = new ModelAndView(INDEX);
        mav.addObject("mostReservedBooks", bookService.findAll());
        accessLogger.accessed("index");
        return mav;
    }
}
