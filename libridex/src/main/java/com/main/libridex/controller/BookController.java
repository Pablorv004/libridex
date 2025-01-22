package com.main.libridex.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.main.libridex.components.logger.AccessLogger;
import com.main.libridex.entity.Book;
import com.main.libridex.entity.Lending;
import com.main.libridex.model.BookDTO;
import com.main.libridex.service.BookService;
import com.main.libridex.service.EmailService;
import com.main.libridex.service.LendingService;
import com.main.libridex.service.ReservationService;

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

    @Autowired
    @Qualifier("reservationService")
    private ReservationService reservationService;

    @Autowired
    @Qualifier("emailService")
    private EmailService emailService;
    
    @GetMapping("/details/{bookId}")
    public String showBookDetails(@PathVariable int bookId, Model model) {
        boolean isLendByUser = lendingService.isLendByUser(bookId);
        boolean isReserved = reservationService.isReserved(bookId);
        boolean isReservedByUser = reservationService.isReservedByUser(bookId);
        boolean isUserTurn = reservationService.isUserTurn(bookId);
        BookDTO bookDTO = bookService.findById(bookId);
        Lending lending = lendingService.findBookCurrentLending(bookId);

        if(isLendByUser && lending != null)
            model.addAttribute("returnDate", lending.getStartDate().plusWeeks(1));
            
        model.addAttribute("book", bookDTO);
        model.addAttribute("isReserved", isReserved);
        model.addAttribute("isReservedByUser", isReservedByUser);
        model.addAttribute("isLendByUser", isLendByUser);
        model.addAttribute("isUserTurn", isUserTurn);

        System.out.println(isUserTurn);

        return DETAILS;
    }

    // RESERVATION ENDPOINTS

    @GetMapping("/reserve/{bookId}")
    public String reserveBook(@PathVariable int bookId, RedirectAttributes flash) {

        if(!reservationService.isReservedByUser(bookId)){
            reservationService.createReservation(bookId);
            flash.addFlashAttribute("success", "Book reserved successfully!");
            return "redirect:/books/catalog";
        }

        flash.addFlashAttribute("error", "You've already reserved this book!");
        return "redirect:/books/details/" + bookId;
    }
    
    @GetMapping("/cancelreserve/{bookId}")
    public String cancelReserve(@PathVariable int bookId, RedirectAttributes flash) {

        if(reservationService.endReservation(bookId)){
            flash.addFlashAttribute("success", "Book reserve cancelled successfully!");
            if(reservationService.findUserCurrentReservation(bookId) != null && reservationService.isUserTurn(bookId))
                emailService.sendEmailReservationAvailable(reservationService.findUserCurrentReservation(bookId).getEmail(), bookService.findById(bookId).getTitle(), bookService.findById(bookId).getImage());
            return "redirect:/books/catalog";
        }

        flash.addFlashAttribute("error", "Something went wrong! Plese contact an administrator.");
        return "redirect:/books/details/" + bookId;
    }


    // LENDING ENDPOINTS

    @GetMapping("/lend/{bookId}")
    public String lendBook(@PathVariable int bookId, RedirectAttributes flash) {
        boolean lentSuccessfully = lendingService.createLending(bookId);

        if(lentSuccessfully){
            flash.addFlashAttribute("success", "Book lent successfully!");
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            emailService.sendEmailLending(email, bookService.findById(bookId).getTitle(), bookService.findById(bookId).getImage());
            reservationService.endReservation(bookId);
            return "redirect:/books/catalog";
        }

        flash.addFlashAttribute("error", "You have too many lendings! Return a book first, please.");
        return "redirect:/books/details/" + bookId;
    }

    @GetMapping("/return/{bookId}")
    public String returnBook(@PathVariable int bookId, RedirectAttributes flash) {
        String email = lendingService.findUserCurrentLending(bookId).getEmail();
        lendingService.endLending(bookId);
        emailService.sendEmailReturn(email, bookService.findById(bookId).getTitle(), bookService.findById(bookId).getImage());
        if(reservationService.findUserCurrentReservation(bookId) != null)
            emailService.sendEmailReservationAvailable(reservationService.findUserCurrentReservation(bookId).getEmail(), bookService.findById(bookId).getTitle(), bookService.findById(bookId).getImage());
        flash.addFlashAttribute("success", "Book returned successfully!");
        return "redirect:/books/catalog";
    }


    // CATALOG ENDPOINTS

    @GetMapping("/catalog")
    public String catalog(@RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) List<String> genres,
            @RequestParam(required = false) List<String> authors,
            @RequestParam(defaultValue = "title_asc") String sortBy,
            @RequestParam(defaultValue = "all") String publishingDateRange,
            @RequestParam(required = false) String query,
            Model model) {
        Page<Book> bookPage;
        if (query != null && !query.isEmpty()) {
            bookPage = bookService.searchBooks(query, page);
        } else {
            bookPage = bookService.findPaginatedWithFilters(page, genres, authors, sortBy, publishingDateRange);
        }
        Map<String, Integer> genresWithAmount = bookService.findGenresWithAmountByBook();
        Map<String, Integer> authorsWithAmount = bookService.findAuthorsWithAmountByBook();
        model.addAttribute("genresWithAmount", genresWithAmount);
        model.addAttribute("authorsWithAmount", authorsWithAmount);
        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("publishingDateRange", publishingDateRange);
        model.addAttribute("query", query);
        accessLogger.accessed("catalog");
        return CATALOG;
    }
}
