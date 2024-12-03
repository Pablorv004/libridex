package com.main.libridex.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.main.libridex.entity.Book;

public interface BookService {
    public List<Book> findAll();

    public Book findById(Integer id);

    public Book save(Book book);

    public void deleteById(Integer id);

    public Page<Book> findAll(PageRequest pageRequest);

}
