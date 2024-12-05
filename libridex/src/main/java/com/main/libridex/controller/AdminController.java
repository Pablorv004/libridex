package com.main.libridex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.main.libridex.components.logger.AccessLogger;
import com.main.libridex.entity.Book;
import com.main.libridex.service.BookService;
import com.main.libridex.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final String ADMIN_VIEW = "admin";
    private static final String BOOKS_VIEW = "adminbooks";
    private static final String BOOKS_FORM = "book_form";
    private static final String USERS_VIEW = "adminusers";

    @Autowired
    @Qualifier("userService")
    private UserService userService;

    @Autowired
    @Qualifier("bookService")
    private BookService bookService;

    @Autowired
    @Qualifier("accessLogger")
    private AccessLogger accessLogger;

    @GetMapping("")
    public String getMainMenuView() {
        return ADMIN_VIEW;
    }
    
    @GetMapping("/books")
    public String getBooksView(Model model, @RequestParam(defaultValue = "0") int page) {
        Page<Book> bookPage = bookService.findAll(PageRequest.of(page, 5));
        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("page", page);
        return BOOKS_VIEW;
    }

    @GetMapping(value = {"/books/form", "/books/form/{id}"})
    public String showForm(Model model, @PathVariable(required = false) Integer id) {
        if(id != null)
            model.addAttribute("book", bookService.findById(id));
        else
            model.addAttribute("book", new Book());

        return BOOKS_FORM;
    }

    @GetMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable(required = true) Integer id, RedirectAttributes flash) {
        bookService.deleteById(id);
        flash.addFlashAttribute("success", "Book deleted successfully!");
        return "redirect:/admin/books";
    }

    @GetMapping("/users")
    public String getUsersView(Model model) {
        
        return USERS_VIEW;
    }
    
}
