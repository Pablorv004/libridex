package com.main.libridex.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            LocalDate startDate = LocalDate.now();
            LocalDate end_date = null;
            Lending lending = new Lending(user, book, startDate, end_date);

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
            if (bookId == lending.getBook().getId() && lending.getEndDate() == null) {
                lending.setEndDate(LocalDate.now());
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
            if (l.getBook().getId() == id && l.getEndDate() == null)
                return l;

        return null;
    }

    @Override
    public User findUserCurrentLending(Integer bookId) {
        for (Lending l : findByBookId(bookId))
            if (l.getBook().getId() == bookId && l.getEndDate() == null)
                return l.getUser();

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
            if (lending.getBook().getId() == bookId && book.isLent() && lending.getEndDate() == null)
                return true;
        }

        return false;
    }

    @Override
    public int countUserActiveLendings(User user, int bookId) {
        int lendings = 0;

        for (Lending lending : user.getLendings())
            if (lending.getBook().getId() == bookId && lending.getEndDate() == null)
                lendings++;

        return lendings;
    }

    @Override
    public List<Lending> findByUserId(Integer userId) {
        return lendingRepository.findByUserId(userId);
    }

    @Override
    public long count() {
        return lendingRepository.count();
    }

    @Override
    public List<Lending> findByUserIdAndEndDateIsNull(Integer userId) {
        return lendingRepository.findByUserIdAndEndDateIsNull(userId);
    }

    @Override
    public List<Lending> findAll() {
        return lendingRepository.findAll();
    }

        @Override
        public Map<User, Integer> filterLendingsPerUser(String searchString) {
        Map<User, Integer> lendingsPerUser = new HashMap<>();
        List<Lending> lendings = lendingRepository.findAll();
        for (Lending lending : lendings) {
            User user = lending.getUser();
            if (user.getName().toLowerCase().contains(searchString.toLowerCase())) {
            lendingsPerUser.put(user, 1 + lendingsPerUser.getOrDefault(user, 0));
            }
        }
        return lendingsPerUser.entrySet().stream()
            .sorted(Map.Entry.<User, Integer>comparingByValue().reversed())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, _) -> e1,
                LinkedHashMap::new));
        }

        @Override
        public Map<Book, Integer> filterLendingsPerBook(String searchString) {
        Map<Book, Integer> lendingsPerBook = new HashMap<>();
        List<Lending> lendings = lendingRepository.findAll();
        for (Lending lending : lendings) {
            Book book = lending.getBook();
            if (book.getTitle().toLowerCase().contains(searchString.toLowerCase())) {
            lendingsPerBook.put(book, 1 + lendingsPerBook.getOrDefault(book, 0));
            }
        }
        return lendingsPerBook.entrySet().stream()
            .sorted(Map.Entry.<Book, Integer>comparingByValue().reversed())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, _) -> e1,
                LinkedHashMap::new));
        }

        @Override
        public Map<String, Integer> countLendingsPerMonth() {
        Map<String, Integer> lendingsPerMonth = new HashMap<>();
        List<Lending> lendings = lendingRepository.findAll();
        for(Lending lending : lendings) {
            String month = lending.getStartDate().getMonth().toString() + " (" + lending.getStartDate().getYear() + ")";
            lendingsPerMonth.put(month, 1 + lendingsPerMonth.getOrDefault(month, 0));
        }
        return lendingsPerMonth;
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
