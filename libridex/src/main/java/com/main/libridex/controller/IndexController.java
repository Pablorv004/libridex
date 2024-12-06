package com.main.libridex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
    public String index(Model model){
        model.addAttribute("latestBooks", bookService.findFirstN(6));
        model.addAttribute("mostReservedBooks", bookService.findFirstN(6));
        accessLogger.accessed("index");
        return INDEX;
    }
}
