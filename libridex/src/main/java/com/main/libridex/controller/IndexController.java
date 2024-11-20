package com.main.libridex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.main.libridex.service.BookService;

@Controller
public class IndexController {

    private static final String INDEX = "index";
    
    @Autowired
    @Qualifier("bookService")
    private BookService bookService;

    @GetMapping("/")
    public String redirectToIndex() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public ModelAndView index(){
        ModelAndView mav = new ModelAndView(INDEX);
        mav.addObject("mostReservedBooks", bookService.findAll());
        return mav;
    }
}
