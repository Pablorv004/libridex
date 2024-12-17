package com.main.libridex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.main.libridex.components.logger.AccessLogger;
import com.main.libridex.entity.Book;
import com.main.libridex.model.BookDTO;
import com.main.libridex.service.BookService;
import com.main.libridex.service.LendingService;
import org.springframework.web.bind.annotation.RequestParam;


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

    @Autowired
    @Qualifier("lendingService")
    private LendingService lendingService;

    @GetMapping("/details/{id}")
    public String showBookDetails(@PathVariable int id, Model model) {
        BookDTO bookDTO = bookService.findById(id);
        model.addAttribute("book", bookDTO);
        return DETAILS;
    }

    @GetMapping("/lend/{bookId}")
    public String lendBook(@PathVariable int bookId, RedirectAttributes flash) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        lendingService.save(bookId, email);
        flash.addFlashAttribute("success", "Book lent successfully!");
        return "redirect:/books/catalog";
    }

    @GetMapping("/return/{bookId}")
    public String returnBook(@PathVariable int bookId, RedirectAttributes flash) {
        lendingService.delete(bookId);
        flash.addFlashAttribute("success", "Book returned successfully!");
        return "redirect:/books/catalog";
    }
    

    @GetMapping("/catalog")
    public String catalog(@RequestParam(defaultValue = "0") int page, Model model){
        Page<Book> bookPage = bookService.findPaginated(page);
        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        accessLogger.accessed("catalog");
        return CATALOG;
    }
}
