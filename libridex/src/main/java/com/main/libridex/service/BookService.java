package com.main.libridex.service;

import java.util.List;

import com.main.libridex.entity.Book;

public interface BookService {
    public List<Book> findAll();
    public Book findById(Integer id);
    public Book save(Book book);
    public void deleteById(Integer id);
}
