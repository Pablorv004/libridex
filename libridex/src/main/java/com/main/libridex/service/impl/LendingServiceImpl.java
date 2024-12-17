package com.main.libridex.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    public void save(Integer bookId, String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        System.out.println(user.getLendings().size());
        Book book = bookRepository.findById(bookId);
        LocalDate start_date = LocalDate.now();
        LocalDate end_date = start_date.plusWeeks(1);
        Lending lending = new Lending(user, book, start_date, end_date);
        book.setLent(true);

        lendingRepository.save(lending);
    }

    @Override
    public void delete(Integer id) {
        bookRepository.findById(id).setLent(false);
        lendingRepository.deleteById(lendingRepository.findByBookId(id).getId());
    }

    @Override
    public List<Lending> getAllLendings() {
        List<LendingDTO> lendingDTOs = new ArrayList<>();
        for(Lending l : lendingRepository.findAll())
            lendingDTOs.add(toDTO(l));
        return lendingRepository.findAll();
    }

    public Lending toEntity(LendingDTO dto) {
        ModelMapper mapper = new ModelMapper();
        return mapper.map(dto, Lending.class);
    }

    public LendingDTO toDTO(Lending lending) {
        ModelMapper mapper = new ModelMapper();
        return mapper.map(lending, LendingDTO.class);
    }
    
}
