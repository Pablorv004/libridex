package com.main.libridex.utils;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.main.libridex.entity.Lending;
import com.main.libridex.repository.BookRepository;
import com.main.libridex.repository.LendingRepository;
import com.main.libridex.service.BookService;
import com.main.libridex.service.EmailService;
import com.main.libridex.service.ReservationService;

@Component
public class BookReturner {
    @Autowired
    @Qualifier("lendingRepository")
    LendingRepository lendingRepository;

    @Autowired
    @Qualifier("bookRepository")
    BookRepository bookRepository;

    @Autowired
    @Qualifier("reservationService")
    private ReservationService reservationService;

    @Autowired
    @Qualifier("emailService")
    private EmailService emailService;

    @Autowired
    @Qualifier("bookService")
    private BookService bookService;

    /**
     * Processes the return of books by checking all lendings in the repository.
     * If a lending's limit date has passed and the book has not been returned,
     * it sets the end date, marks the book as not lent, updates the
     * repositories and sends an email to the user notificating about the book return.
     * Additionally, if there is a reservation for the book, it sends an email
     * notification to the next user in the reservation queue.
     */
    public void returnBooks() {
        List<Lending> lendings = lendingRepository.findAll();
        LocalDate limitDate = null;
        Integer bookId = null;
        for (Lending l : lendings) {
            limitDate = l.getStartDate().plusWeeks(1).plusDays(1);
            bookId = l.getBook().getId();
            if (limitDate.isBefore(LocalDate.now()) && l.getEndDate() == null) {
                l.setEndDate(limitDate);
                l.getBook().setLent(false);
                bookRepository.save(l.getBook());
                lendingRepository.save(l);

                // Send email to the user notificating about the late book return
                emailService.sendEmailReturnLate(l.getUser().getEmail(), bookService.findById(bookId).getTitle(), bookService.findById(bookId).getImage());

                // Send email telling the next user with a reserve that the book is available
                // for lending
                if (reservationService.findUserCurrentReservation(bookId) != null)
                    emailService.sendEmailReservationAvailable(reservationService.findUserCurrentReservation(bookId).getEmail(),
                            bookService.findById(bookId).getTitle(),
                            bookService.findById(bookId).getImage());
            }
        }
    }
}
