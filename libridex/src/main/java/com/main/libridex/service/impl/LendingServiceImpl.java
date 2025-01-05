package com.main.libridex.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.main.libridex.entity.Book;
import com.main.libridex.entity.Lending;
import com.main.libridex.entity.User;
import com.main.libridex.model.LendingDTO;
import com.main.libridex.repository.BookRepository;
import com.main.libridex.repository.LendingRepository;
import com.main.libridex.repository.UserRepository;
import com.main.libridex.service.LendingService;

@Service("lendingService")
public class LendingServiceImpl implements LendingService {

    @Autowired
    @Qualifier("lendingRepository")
    LendingRepository lendingRepository;

    @Autowired
    @Qualifier("userRepository")
    UserRepository userRepository;

    @Autowired
    @Qualifier("bookRepository")
    BookRepository bookRepository;


    @Override
    public boolean createLending(Integer bookId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);

        if (countUserActiveLendings(user, bookId) < 5) {
            Book book = bookRepository.findById(bookId);
            LocalDate start_date = LocalDate.now();
            LocalDate end_date = null;
            Lending lending = new Lending(user, book, start_date, end_date);

            book.setLent(true);
            lendingRepository.save(lending);
            return true;
        }

        return false;
    }

    @Override
    public void endLending(Integer bookId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);

        for (Lending lending : user.getLendings()) {
            if (bookId == lending.getBook().getId() && lending.getEnd_date() == null) {
                lending.setEnd_date(LocalDate.now());
                lendingRepository.save(lending);
                break;
            }
        }

        Book book = bookRepository.findById(bookId);
        if (book != null) {
            book.setLent(false);
            bookRepository.save(book);
        }
    }

    @Override
    public List<Lending> findByBookId(Integer bookId) {
        return lendingRepository.findByBookId(bookId);
    }

    @Override
    public Lending findBookCurrentLending(Integer id) {
        for (Lending l : findByBookId(id))
            if (l.getBook().getId() == id && l.getEnd_date() == null)
                return l;

        return null;
    }

    @Override
    public List<Lending> getAllLendings() {
        List<LendingDTO> lendingDTOs = new ArrayList<>();
        for (Lending lending : lendingRepository.findAll())
            lendingDTOs.add(toDTO(lending));
        return lendingRepository.findAll();
    }

    @Override
    public boolean isLendByUser(Integer bookId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        Book book = bookRepository.findById(bookId);

        for (Lending lending : user.getLendings()) {
            if (lending.getBook().getId() == bookId && book.isLent() && lending.getEnd_date() == null)
                return true;
        }

        return false;
    }

    @Override
    public int countUserActiveLendings(User user, int bookId) {
        int lendings = 0;

        for(Lending lending : user.getLendings())
            if(lending.getBook().getId() == bookId && lending.getEnd_date() == null)
                lendings++;

        return lendings;
    }

    @Override
    public List<Lending> findByUserId(Integer userId) {
        return lendingRepository.findByUserId(userId);
    }

    // MODEL MAPPERS

    public Lending toEntity(LendingDTO dto) {
        ModelMapper mapper = new ModelMapper();
        return mapper.map(dto, Lending.class);
    }

    public LendingDTO toDTO(Lending lending) {
        ModelMapper mapper = new ModelMapper();
        return mapper.map(lending, LendingDTO.class);
    }

}
