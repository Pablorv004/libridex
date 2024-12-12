package com.main.libridex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.main.libridex.components.logger.AccessLogger;
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

    @GetMapping("")
    public String getMainMenuView() {
        return ADMIN_VIEW;
    }

    // USERS RELATED ENDPOINTS

    @GetMapping("/users")
    public String getUsersView(Model model, @RequestParam(defaultValue = "0") int page) {
        Page<SecureUserDTO> userPage = userService.findAll(PageRequest.of(page, 5));
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("page", page);
        return USERS_VIEW;
    }

    @GetMapping("/users/toggleactivation/{id}")
    public String toggleUserActivation(@PathVariable Integer id, RedirectAttributes flash) {
        userService.toggleActivation(id);
        flash.addFlashAttribute("success", "User activation status updated successfully!");
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Integer id, RedirectAttributes flash) {
        userService.deleteById(id);
        flash.addFlashAttribute("success", "User deleted successfully!");
        return "redirect:/admin/users";
    }

    // BOOKS RELATED ENDPOINTS

    @GetMapping("/books")
    public String getBooksView(Model model, @RequestParam(defaultValue = "0") int page) {
        Page<Book> bookPage = bookService.findAll(PageRequest.of(page, 5));
        bookPage.forEach(book -> {
            if(!book.getImage().isEmpty()){
                String imageUrl = MvcUriComponentsBuilder
                        .fromMethodName(FileController.class, "serveFile", book.getImage())
                        .build()
                        .toUriString();
                book.setImage(imageUrl);
            }else {
                String imageUrl = MvcUriComponentsBuilder
                        .fromMethodName(FileController.class, "serveFile", "default_image.png")
                        .build()
                        .toUriString();
                book.setImage(imageUrl);
            }
        });
        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("page", page);
        return BOOKS_VIEW;
    }

    @GetMapping(value = { "/books/form", "/books/form/{id}" })
    public String showForm(Model model, @PathVariable(required = false) Integer id) {
        if (id != null)
            model.addAttribute("book", bookService.findById(id));
        else
            model.addAttribute("book", new BookDTO());

        return BOOKS_FORM;
    }

    @GetMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable(required = true) Integer id, RedirectAttributes flash) {
        bookService.deleteById(id);
        flash.addFlashAttribute("success", "Book deleted successfully!");
        return "redirect:/admin/books";
    }

    @PostMapping(value = { "/books/add", "/books/edit" })
    public String addBook(@Valid @ModelAttribute("book") BookDTO bookDTO, BindingResult bResult,
            @RequestParam("imageFile") MultipartFile imageFile, RedirectAttributes flash) {

        bookService.checkExistentBook(bookDTO, bResult);

        if (bResult.hasErrors()) {
            flash.addFlashAttribute("error", "Oops! Something went wrong!");
            return BOOKS_FORM;
        }

        bookService.setImage(bookDTO, imageFile);
        String message = bookDTO.getId() == null ? "Book created succesfully!" : "Book edited successfully!";

        bookService.save(bookDTO);
        flash.addFlashAttribute("success", message);
        return "redirect:/admin/books";

    }

}
