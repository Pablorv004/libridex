package com.main.libridex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.main.libridex.components.logger.AccessLogger;
import com.main.libridex.model.BookDTO;
import com.main.libridex.service.BookService;

@Controller
@RequestMapping("/books")
public class BookController {
    private static final String CATALOG = "catalog";
    private static final String DETAILS = "book_details";

    @Autowired
    @Qualifier("accessLogger")
    private AccessLogger accessLogger;
    
    @Autowired
    @Qualifier("bookService")
    private BookService bookService;

    @GetMapping("/details/{id}")
    public String showBookDetails(@PathVariable int id, Model model){
        BookDTO bookDTO = bookService.findById(id);
        model.addAttribute("book", bookDTO);
        return DETAILS;
    }

    @GetMapping("/catalog")
    public String catalog(Model model){
        model.addAttribute("books", bookService.findAll());
        accessLogger.accessed("catalog");
        return CATALOG;
    }
}
