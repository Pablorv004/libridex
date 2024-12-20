package com.main.libridex.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.main.libridex.components.logger.AccessLogger;
import com.main.libridex.components.logger.BookLogger;
import com.main.libridex.components.logger.UserLogger;
import com.main.libridex.entity.Book;
import com.main.libridex.model.BookDTO;
import com.main.libridex.model.SecureUserDTO;
import com.main.libridex.service.BookService;
import com.main.libridex.service.UserService;

import jakarta.validation.Valid;

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

    @Autowired
    @Qualifier("userLogger")
    private UserLogger userLogger;

    @Autowired
    @Qualifier("bookLogger")
    private BookLogger bookLogger;

    @GetMapping("")
    public String getMainMenuView() {
        return ADMIN_VIEW;
    }

    // USERS RELATED ENDPOINTS

    @GetMapping("/users")
    public String getUsersView(Model model, @RequestParam(defaultValue = "0") int page) {
        List<SecureUserDTO> userPage = userService.findAllSecureUsers();
        model.addAttribute("users", userPage);
        accessLogger.accessed("admin/users");
        return USERS_VIEW;
    }

    @GetMapping("/users/toggleactivation/{id}")
    public String toggleUserActivation(@PathVariable Integer id, RedirectAttributes flash) {
        userService.toggleActivation(id);
        userLogger.changedStatus(String.valueOf(id));
        flash.addFlashAttribute("success", "User activation status updated successfully!");
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Integer id, RedirectAttributes flash) {
        userService.deleteById(id);
        userLogger.deleted(String.valueOf(id));
        flash.addFlashAttribute("success", "User deleted successfully!");
        return "redirect:/admin/users";
    }

    // BOOKS RELATED ENDPOINTS

    @GetMapping("/books")
    public String getBooksView(Model model) {
        List<Book> bookPage = bookService.findAll();
        accessLogger.accessed("admin/books");
        model.addAttribute("books", bookPage);
        return BOOKS_VIEW;
    }

    @GetMapping(value = { "/books/form", "/books/form/{id}" })
    public String showForm(Model model, @PathVariable(required = false) Integer id) {
        if (id != null) {
            BookDTO bookDTO = bookService.findById(id);
            model.addAttribute("book", bookDTO);
            accessLogger.accessed("admin/books/form with Book: " + id);
        } else {
            model.addAttribute("book", new BookDTO());
            accessLogger.accessed("admin/books/form with new Book");
        }
        return BOOKS_FORM;
    }

    @GetMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable(required = true) Integer id, RedirectAttributes flash) {
        bookService.deleteById(id);
        bookLogger.deleted(String.valueOf(id));
        flash.addFlashAttribute("success", "Book deleted successfully!");
        return "redirect:/admin/books";
    }

    @PostMapping(value = { "/books/add", "/books/edit" })
    public String addBook(@Valid @ModelAttribute("book") BookDTO bookDTO, BindingResult bResult,
            @RequestParam MultipartFile imageFile, RedirectAttributes flash) {
        bookService.checkExistentBook(bookDTO, bResult);

        if (bResult.hasErrors()) {
            flash.addFlashAttribute("error", "Oops! Something went wrong!");
            bookLogger.failedToAdd(bookDTO.getTitle(), new Exception("Invalid book data. Check the fields."));
            return BOOKS_FORM;
        }

        bookService.setImage(bookDTO, imageFile);
        String message = bookDTO.getId() == null ? "Book created succesfully!" : "Book edited successfully!";
        bookService.save(bookDTO);
        bookLogger.added(bookDTO.getTitle());
        flash.addFlashAttribute("success", message);
        return "redirect:/admin/books";

    }

}
