package com.main.libridex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.main.libridex.components.logger.AccessLogger;
import com.main.libridex.service.BookService;

@Controller
public class CatalogController {

    private static final String CATALOG = "catalog";

    @Autowired
    @Qualifier("accessLogger")
    private AccessLogger accessLogger;
    
    @Autowired
    @Qualifier("bookService")
    private BookService bookService;

    @GetMapping("/catalog")
    public String catalog(Model model){
        model.addAttribute("books", bookService.findAll());
        accessLogger.accessed("catalog");
        return CATALOG;
    }
}
