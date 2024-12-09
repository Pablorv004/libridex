package com.main.libridex.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.main.libridex.entity.Book;
import com.main.libridex.model.BookDTO;

public interface BookService {
    List<Book> findAll();

    Page<Book> findAll(PageRequest pageRequest);

    Page<Book> findFirstN(int elementsNumber);
    
    BookDTO findById(Integer id);

    Book save(BookDTO bookDTO);

    void deleteById(Integer id);

    void checkExistentBook(BookDTO bookDTO, BindingResult bResult);

    String saveImage(MultipartFile imageFile);

    void setImage(BookDTO bookDTO, MultipartFile imageFile);
}
