package com.main.libridex.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.main.libridex.entity.Book;
import com.main.libridex.repository.BookRepository;
import com.main.libridex.service.BookService;

@Service("bookService")
public class BookServiceImpl implements BookService{

    @Autowired
    @Qualifier("bookRepository")
    BookRepository bookRepository;

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public Book findById(Integer id) {
        return bookRepository.findById(id).orElse(null);
    }

    @Override
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public void deleteById(Integer id) {
        bookRepository.deleteById(id);
    }
    
}
